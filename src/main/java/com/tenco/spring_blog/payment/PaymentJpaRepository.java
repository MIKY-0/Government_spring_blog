package com.tenco.spring_blog.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PaymentJpaRepository extends JpaRepository<Payment , Long> {

    @Query("select p from Payment p where p.paymentId = :paymentId")
    Optional<Payment> findByPaymentId(@Param("paymentId") String paymentId);

    @Query("""
        select case when count(p) > 0 then true else false end from Payment p
        where p.paymentId = :paymentId
""")
    boolean existsByPaymentId(@Param("paymentId") String paymentId);

    // 결제 내역 조회(로그인 사용자)
    @Query("""
        select p from Payment p 
        where p.user.id = :userId and p.status = :status
        order by p.id
""")
    List<Payment> findAllByUserIdAndStatus(@Param("userId") Long userId , @Param("status") PaymentStatus status);

}
