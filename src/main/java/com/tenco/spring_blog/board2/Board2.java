package com.tenco.spring_blog.board2;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Data     @Table(name = "board_tb")     @Entity
public class Board2 {
    @Id     @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;
    private String username;
    private Timestamp createdAt;
}
