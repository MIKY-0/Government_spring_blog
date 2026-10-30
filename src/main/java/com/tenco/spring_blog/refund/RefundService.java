package com.tenco.spring_blog.refund;

import com.tenco.spring_blog._core.error.Exception400;
import com.tenco.spring_blog._core.error.Exception403;
import com.tenco.spring_blog._core.error.Exception404;
import com.tenco.spring_blog.payment.Payment;
import com.tenco.spring_blog.payment.PaymentJpaRepository;
import com.tenco.spring_blog.payment.PaymentResponse;
import com.tenco.spring_blog.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service     @Slf4j     @RequiredArgsConstructor    @Transactional(readOnly = true)
public class RefundService {
    private final PaymentJpaRepository paymentJpaRepository;
    private final RefundJpaRepository refundJpaRepository;

    /**
     * 환불 요청폼에 보여줄 결제 정보.
     */
    public PaymentResponse.ListDto findRequestForm(Long paymentPK , Long userId) {
       // 환불 요청 가능 여부 확인.
       Payment payment = checkRefundable(paymentPK , userId);

       // 응답 정상 동작하면 응답DTO 생성 후 리턴.  checkRefundable에서 이미 환불가능한 건만 필터링했으므로 true처리.
        return new PaymentResponse.ListDto(payment , true);
    }

    private Payment checkRefundable(Long paymentPK , Long userId) {
        // 1. payment 테이블에 결제 내역 조회.
        Payment payment = paymentJpaRepository.findById(paymentPK)
                .orElseThrow(() -> new Exception404("결제내역을 찾을 수 없습니다"));

        // 2. 본인 결제인지 확인.
        if(!payment.isOwner(userId)) throw new Exception403("본인 결제만 환불 요청 가능합니다");

        // 3. 결제 완료 상태만 환불 가능.(READY , CANCELED는 환불 불가)
        if(!payment.isPaid()) throw new Exception400("결제 완료된 건만 환불 요청할 수 있습니다");

        // 4. 환불 요청한 결제가 맞는지 확인. (대기중 , 승인됨 , 거절됨 모두 신청 불가)
        if(refundJpaRepository.findByPaymentPk(paymentPK).isPresent()) {
            throw new Exception400("이미 환불 요청했습니다");
        }
        return payment;
    }

    // 환불 요청 저장.
    @Transactional
    public void save(Long userId , RefundRequest.SaveDto saveDto) {
        // 1. 폼 요청 화면처럼 검사를 한번 더 실행. (주소 직접 요청 , 중복 제출 방지)
        Payment payment = checkRefundable(saveDto.getPaymentPk(), userId);

        // 2. 환불 요청 저장.
        Refund refund = Refund.builder()
                .user(payment.getUser())
                .reason(saveDto.getReason().trim())
                .payment(payment)
                .build();
        refundJpaRepository.save(refund);
    }

    // 환불 요청 내역 조회.
    public List<RefundResponse.RefundListDto> findAllByUserId(Long sessionUserId) {
        List<Refund> refundList = refundJpaRepository.findAllByUserIdJoinPayment(sessionUserId);

        return refundList.stream().map(RefundResponse.RefundListDto::new).toList();
    }


    // 관리자에게 환불요청내역 조회.
    public List<RefundResponse.AdminListDto> findAllForAdmin() {
        List<Refund> refundList = refundJpaRepository.findAllJoinUserAndPayment();

        return refundList.stream().map(RefundResponse.AdminListDto::new).toList();
    }

    @Transactional
    public void reject(Long refundId, String rejectReason) {
        // 1. 넘겨 받은 refundId 기준으로 조회부터. 엔티티 정보를 업데이트 하려고.
        Refund refund = refundJpaRepository.findById(refundId).orElseThrow(() -> new Exception404("환불요청을 찾을 수 없음"));

        // 2. 이미 승인 , 거절된 요청은 또 처리할 필요없음.
        if(!refund.isPending()) throw new Exception400("이미 처리된 환불 요청입니다");

        refund.reject(rejectReason);
    }

    /**
     * 관리자 승인 1단계 : 승인 가능한지 검사
     * @param refundId
     * @return : 포트원 결제 취소에 필요한 값.
     */
    public RefundResponse.ApproveDto checkApprove(Long refundId) {
        // refund엔티티 + 요청자의 남은 포인트 가져와야함.
        Refund refund = refundJpaRepository.findByIdJoinUserAndPayment(refundId)
                .orElseThrow(() -> new Exception404("환불 요청을 찾을 수 없습니다"));

        if(!refund.isPending()) throw new Exception400("이미 처리된 환불 요청입니다");

        // 결제(PAID , READY)
        Payment payment = refund.getPayment();
        if(!payment.isPaid()) throw new Exception400("결제되지 않은 요청입니다");

        // 유저가 충전한 포인트를 이미 사용했다면 돌려받을 포인트가 없어서 환불 불가.
        User user = refund.getUser();
        if(user.getPoint() < payment.getAmount()) throw new Exception400("포인트 잔액이 부족하여 환불할 수 없습니다");

        return new RefundResponse.ApproveDto(payment.getPaymentId() , payment.getAmount());
    }

    // 환불 승인 DB 반영
    @Transactional
    public void approve(Long refundId) {
        Refund refund = refundJpaRepository.findByIdJoinUserAndPayment(refundId)
                .orElseThrow(() -> new Exception404("환불 요청을 찾을 수 없습니다"));

        // PortOneRefundService의 1 ~ 4번 실행중 다른 요청이 같은 건을 처리할 수 있으므로 한번 더 확인.
        if(!refund.isPending()) throw new Exception400("대기중인 환불 요청만 환불 승인 신청 가능");
        Payment payment = refund.getPayment();

        // 포인트 차감.
        refund.getUser().deductPoint(payment.getAmount());

        // Canceled로 변경.
        payment.markCancelled();

        // APPROVED로 변경.
        refund.approve();
    }
}
