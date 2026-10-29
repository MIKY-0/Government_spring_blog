package com.tenco.spring_blog.payment;

import com.tenco.spring_blog._core.error.Exception400;
import com.tenco.spring_blog._core.error.Exception403;
import com.tenco.spring_blog.refund.Refund;
import com.tenco.spring_blog.refund.RefundJpaRepository;
import com.tenco.spring_blog.refund.RefundService;
import com.tenco.spring_blog.user.User;
import com.tenco.spring_blog.user.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service        @RequiredArgsConstructor      @Transactional(readOnly = true)
public class PaymentService {
    private final PaymentJpaRepository paymentJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final RefundJpaRepository refundJpaRepository;

    public List<PaymentResponse.ListDto> findAllByUserId(Long userId) {
        List<Payment> paymentList = paymentJpaRepository
                .findAllByUserIdAndStatusList(userId , List.of(PaymentStatus.PAID , PaymentStatus.CANCELED));

        // 2. 결제 완료 상태면 환불 가능. 아니면 불가. 추후 연산해서 만들어서 내려준다.
        // 응답 DTO 설계 후 리턴.  환불 테이블에 내가 요청한 PK정보만 리스트로 뽑기.

        List<Long> paymentId = refundJpaRepository.findPaymentPkListByUserId(userId);

        return paymentList.stream()
                .map(p -> new PaymentResponse.ListDto(p ,
                        !paymentId.contains(p.getId()) && p.getStatus() != PaymentStatus.CANCELED))
                .toList();
    }

    /*
    1단계 : 결제 건 생성.
    paymentId를 발급하고 "누가 얼마를" 결제할 지 READY상태로 저장.
    Id만들고 DB에 저장 , 만든 Id 반환
     */
    @Transactional
    public String prepare(Long sessionUserId, Integer amount) {
        User user = userJpaRepository.findById(sessionUserId).orElseThrow(() -> new Exception400("사용자를 찾을 수 없습니다"));
        
        // 식별자가 겹치면 다시 만듦.
        String paymentId = generatedPaymentId(sessionUserId);
        while(paymentJpaRepository.existsByPaymentId(paymentId)) {
            paymentId = generatedPaymentId(sessionUserId);
        }

        // READY 상태로 태어남.
        Payment payment = Payment.builder()
                .paymentId(paymentId)
                .user(user)
                .amount(amount)
                .build();

        paymentJpaRepository.save(payment);

        return paymentId;
    }

    // paymentId 생성 편의 메서드.
    private static String generatedPaymentId(Long userId) {
        String paymentId = "point_" + userId + "_" + System.currentTimeMillis() + "_" +
                UUID.randomUUID().toString().substring(0 , 8);
        return paymentId;
    }


    /*
    3단계 : 결제 검증 + 포인트 충전
    포트원에서 조회할 결과를 1단계 기록 payment_tb 정보랑 비교.
    하나라도 맞지 않으면 예외 --> 트랜잭션 롤백(포인트 지급 안됨)
     */
    @Transactional
    public PaymentResponse.CompleteDto complete(Long sessionUserId, String paymentId, PaymentResponse.PortOnePayment portOnePayment) {
        // 1. 우리 서버가 발급한 결제 건인지 확인.
        Payment payment = paymentJpaRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new Exception400("결제 정보를 찾을 수 없습니다"));

        // 2. 결제를 시작한 본인인지 확인.
        if(!payment.isOwner(sessionUserId)) throw new Exception403("본인의 결제만 처리할 수 있습니다");

        // 3. 중복 지급 방지 : 이미 PAID 상태라면 거부.
        if(payment.isPaid()) throw new Exception400("이미 처리된 결제입니다");

        // 4. 포트원 기준 결제 상태 확인.
        if(!"PAID".equals(portOnePayment.getStatus())) throw new Exception400("결제가 완료되지 않았습니다 : " + portOnePayment.getStatus());

        // 5. 금액 위변조 확인 : 실제 결제 금액 == 1단계에서 기록한 금액이 동일해야함.
        if(portOnePayment.getAmount() == null ||
           portOnePayment.getAmount().getTotal() == null ||
           portOnePayment.getAmount().getTotal() != payment.getAmount())  {
            throw new Exception400("결제 금액이 일치하지 않습니다");
        }

        // 6. 여기까지 통과했다면 포인트충전 + 결제 완료 표시(더티체킹)
        User user = payment.getUser();
        user.chargePoint(payment.getAmount()); // 해당 유저의 포인트 충전됨.
        payment.markPaid(portOnePayment.getPgTxId());

        log.info("결제 완료 및 포인트 충전 - 사용자 Id : {} , paymentId : {} , 금액 : {}" ,
                sessionUserId , paymentId , payment.getAmount());

        return new PaymentResponse.CompleteDto(payment.getAmount() , user.getPoint());
    }
}
