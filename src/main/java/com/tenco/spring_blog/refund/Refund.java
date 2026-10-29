package com.tenco.spring_blog.refund;

import com.tenco.spring_blog.payment.Payment;
import com.tenco.spring_blog.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Getter     @Entity     @NoArgsConstructor
@Table(name = "refund_tb")
public class Refund {
    @Id     @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)      @JoinColumn(name = "user_id" , nullable = false)
    private User user;

    // 환불 정책) (1건의 결제) : (1건의 환불) = 1 : 1. --> 지금은 1대1이지만 만약 추후 부분환불(1:N)로 변경될 가능성도 생각해야함.
    // 1:1이라면 @OneToOne이지만 추후 변경될 가능성을 생각하고 @ManyToOne으로 세팅해놓고 unique를 걸어주면됨.
    @ManyToOne(fetch = FetchType.LAZY)      @JoinColumn(name = "payment_id" , nullable = false , unique = true)
    private Payment payment;

    @Column(nullable = false , length = 500)
    private String reason; // 사용자의 환불 사유.

    // 환불 상태 : 최초 상태는 '대기'. 관리자 판단에 따라 '승인','거절'로 업뎃.(더티체킹으로 처리)
    @Enumerated(EnumType.STRING)   @Column(nullable = false , length = 20)
    private RefundStatus status;

    @Column(length = 500)
    private String rejectReason; // 관리자가 거절했을때 거절 사유. 거절할 때만 사용하므로 null 허용.

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt; // 마지막 변경 시각('승인','거절' 시간)

    @Builder
    public Refund(User user , Payment payment , String reason) {
        this.user = user;
        this.payment = payment;
        this.reason = reason;
        this.status = RefundStatus.PENDING;
    }

    // [[편의 메서드 미리 생성.]]
    // 관리자가 아직 처리하지 않은 요청인지 확인.
    public boolean isPending() {return this.status == RefundStatus.PENDING;}

    // 승인 처리.(트랜잭션 안에서 호출하면 더티체킹으로 UPDATE처리됨)
    public void approve() {this.status = RefundStatus.APPROVED;}

    // 거절 + 거절 사유.
    public void reject(String rejectReason) {
        this.status = RefundStatus.REJECTED;
        this.rejectReason = rejectReason;
    }
}
