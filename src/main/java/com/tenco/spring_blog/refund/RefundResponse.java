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
}
