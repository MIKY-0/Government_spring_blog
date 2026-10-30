package com.tenco.spring_blog.refund;

import com.tenco.spring_blog._core.error.Exception400;
import com.tenco.spring_blog.payment.PaymentResponse;
import com.tenco.spring_blog.payment.PortOneApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

// 환불 승인 처리 순서 관리 : 검사 -> 포트원 취소 -> 응답확인 -> DB 반영
// 트랜잭션을 걸지 않으려고 PortOneRefundService으로 따로 만듦. --> 커넥션 풀을 외부가 점유 하지 않도록.
@Service        @RequiredArgsConstructor
public class PortOneRefundService {
    private final RefundService refundService;
    private final PortOneApiClient portOneApiClient;

    public void approve(Long refundId) {
        // 1. 승인 가능 여부 검사.(DB 조회만)
        RefundResponse.ApproveDto approveDto = refundService.checkApprove(refundId);

        PaymentResponse.PortOneCancel portOneCancel;
        try {
        // 2. 포트원 결제 취소 요청(외부 통신 , 트랜잭션 밖)
            portOneCancel = portOneApiClient.cancelPayment(approveDto.getPaymentId() , approveDto.getAmount());
        } catch (RestClientResponseException e) { // 400 , 401 , 403 , 404 , 409 -- 포트원이 오류 상태 코드로 응답.
            throw new RuntimeException(e);
        } catch(RestClientException e) { // 500 , 502 -- 응답을 받지 못함(연결 실패 , 강제종료 , 시간초과 등)
            throw new RuntimeException(e);
        }

        // 3. 포트원 취소 완료 상태 확인. (포트원 응답이 "SUCCEEDED"가 아니면 DB반영안함)
        if(portOneCancel == null ||
           portOneCancel.getCancellation() == null ||
           !"SUCCEEDED".equals(portOneCancel.getCancellation().getStatus())) {
            throw new Exception400("결제취소가 완료되지 않았습니다. 잠시후 포트원 콘솔에서 확인해주세요");
        }

        // 4. DB반영.(포인트 차감 , 결제 상태 "CANCELED"로 변경 , 환불 상태 "APPROVED"로 변경)
        refundService.approve(refundId);
    }
}
