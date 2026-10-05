package com.tenco.spring_blog.board2;

import lombok.Data;

public class BoardRequest2 {

    @Data
    public static class SaveDto{
        private String username;
        private String title;
        private String content;

        public void validate(){
            boolean empty = (username == null || username.trim().isEmpty() ||
                             title == null || title.trim().isEmpty()  ||
                             content == null || content.trim().isEmpty());

            if(empty) throw new IllegalArgumentException("이름 , 제목 , 내용은 비어있을 수 없습니다.");
        }
    }

    @Data
    public static class UpdateDto {
        private String title;
        private String content;

        public void validate(){
            boolean empty = (title == null || title.trim().isEmpty()  ||
                             content == null || content.trim().isEmpty());

            if(empty) throw new IllegalArgumentException("제목 , 내용은 비어있을 수 없습니다.");
        }
    }
}
