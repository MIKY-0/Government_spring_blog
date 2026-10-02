package com.tenco.spring_blog.board2;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;


@Data   @Table(name = "board_tb")       @Entity
@Builder        @AllArgsConstructor     @NoArgsConstructor
public class Board2 {
    @Id     @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String title;
    private String content;

    @CreationTimestamp
    private Timestamp createdAt;


    public void update(BoardRequest2.UpdateDto req){
            this.title = req.getTitle();
            this.content = req.getContent();

            req.validateUpdate();
    }
}
