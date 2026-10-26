package com.tenco.spring_blog.purchase;

import com.tenco.spring_blog.board.Board;
import com.tenco.spring_blog.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Getter     @AllArgsConstructor     @NoArgsConstructor
@Entity     @Builder
@Table(name = "purchase_tb" ,
        uniqueConstraints = {@UniqueConstraint(name = "uk_user_board" , columnNames = {"user_id" , "board_id"})}) // unique 설정.
public class Purchase {

    @Id     @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)  @JoinColumn(name = "user_id" , nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)  @JoinColumn(name = "board_id" , nullable = false)
    private Board board;

    @CreationTimestamp
    private Timestamp createdAt;

    // 구매 당시 지불한 포인트.
    private int price;

    public Purchase(User user, Board board , int price) {
        this.user = user;
        this.board = board;
        this.price = price;
    }
}
