package com.tenco.spring_blog.payment;

import com.tenco.spring_blog.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

// 결제 내역 엔티티. (포트원 v2)
// paymentId : 우리 서버가 발급한 결제 건 식별자.(유니크 , 중복 지급 방지)
// amount : 결제 예정 금액 (실제 결제는 포트원에서 처리함 -> 포트원에서 응답 -> 실제 결제 금액과 우리사이트 결제 금액이 동일한지 비교)
@Getter     @AllArgsConstructor     @NoArgsConstructor
@Entity     @Table(name = "payment_tb")
public class Payment {
    @Id     @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 결제 건당 식별자(같은 값이 두번 저장되지 않도록 유니크)
    @Column(unique = true , nullable = false , length = 100)
    private String paymentId;

    // PG사 거래번호(결제완료 되면 포트원 사이트에서 응답을 받음 , 추후 '추적'용)
    private String pgTxId;

    // 결제한 사용자.
    @ManyToOne(fetch = FetchType.LAZY)  @JoinColumn(name = "user_id" , nullable = false)
    private User user;

    // 결제 금액(충전할 포인트)
    @Column(nullable = false)
    private int amount;

    @Enumerated(EnumType.STRING)    @Column(nullable = false , length = 20)
    private PaymentStatus status; // 결제 대기 , 환불 , 완료.

    @CreationTimestamp
    private Timestamp createdAt;

    @Builder
    public Payment(String paymentId , User user , int amount) {
        this.paymentId = paymentId;
        this.user = user;
        this.amount = amount;
        this.status = PaymentStatus.READY; // 처음에는 항상 결제 예정.
    }

    // 편의 메서드.
    // 결제 요청한 사용자인지 확인.
    public boolean isOwner(Long userId) {
        return this.user.getId().equals(userId);
    }

    // 이미 처리(포인트 지급)된 결제인지 확인.
    public boolean isPaid() {
        return this.status == PaymentStatus.PAID;
    }

    // 추후 검증 통과 후 결제 완료로 변경처리(더티체킹)
    public void markPaid(String pgTxId) {
        this.status = PaymentStatus.PAID; // 결제상태를 완료로 변경.
        this.pgTxId = pgTxId;
    }

}
