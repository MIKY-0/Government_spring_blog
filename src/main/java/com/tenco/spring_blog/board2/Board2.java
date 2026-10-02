package com.tenco.spring_blog.board2;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Table(name = "board_tb")       @Data       @Entity
@AllArgsConstructor     @NoArgsConstructor      @Builder
public class Board2 {
    @Id     @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    private Timestamp createdAt;

    private String title;
    private String content;
    private String username;



    public void update(BoardRequest2.UpdateDto reqDto) {
        this.title = reqDto.getTitle();
        this.content = reqDto.getContent();
    }
}
