package com.tenco.spring_blog.refund;

import com.tenco.spring_blog._core.util.MyDateUtil;
import lombok.Builder;
import lombok.Data;

public class RefundResponse {
    @Data
    public static class RefundListDto {
        private Long id;
        private String paymentId;
        private String statusDisplay;
        private String reason;
        private String rejectReason;
        private String requestedAt;
        private int amount;
        private boolean rejected;
        private boolean pending;
        private boolean approved;

        public RefundListDto(Refund refund) {
            this.id = refund.getId();
            this.paymentId = refund.getPayment().getPaymentId();
            this.statusDisplay = refund.getStatus().getDisplayname();
            this.reason = refund.getReason();
            this.amount = refund.getPayment().getAmount();

            if(refund.getRejectReason() != null) this.rejectReason = refund.getRejectReason();
            if (refund.getCreatedAt() != null) this.requestedAt = MyDateUtil.timestampFormat(refund.getCreatedAt());

            this.pending = statusDisplay.equals(RefundStatus.PENDING.getDisplayname());
            this.approved = statusDisplay.equals(RefundStatus.APPROVED.getDisplayname());
            this.rejected = statusDisplay.equals(RefundStatus.REJECTED.getDisplayname());
        }
    }

    @Data
    public static class AdminListDto {
        private Long id;  // 환불 요청 PK.
        private String username;  // 환불 신청자.
        private String paymentId;  // 포트원 결제건 식별자.
        private int amount;  // 환불 금액.
        private String reason;  // 신청자 환불 사유.
        private String statusDisplay;   // 대기중 , 승인됨 , 거절됨.
        private String requestedAt; // 환불 신청일.

        private boolean pending;  // 대기중 여부.
        private boolean approved;  // 승인됨 여부.


        public AdminListDto(Refund refund) {
            this.id = refund.getId();
            this.username = refund.getUser().getUsername();
            this.paymentId = refund.getPayment().getPaymentId();
            this.amount = refund.getPayment().getAmount();
            this.reason = refund.getReason();
            this.statusDisplay = refund.getStatus().getDisplayname();
            this.pending = refund.getStatus() == RefundStatus.PENDING;
            this.approved = refund.getStatus() == RefundStatus.APPROVED;

            if(refund.getCreatedAt() != null) this.requestedAt = MyDateUtil.timestampFormat(refund.getCreatedAt());
        }
    }

    @Data
    public static class ApproveDto {
        private String paymentId; // 포트원 결제 건 식별자(경로로 활용됨)
        private int amount;  // DB 조회해야 알 수 있음.

        public ApproveDto(String paymentId, int amount) {
            this.paymentId = paymentId;
            this.amount = amount;
        }
    }
}
