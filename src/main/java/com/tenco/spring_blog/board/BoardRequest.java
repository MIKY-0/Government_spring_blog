//package com.tenco.spring_blog.board;
//
//import lombok.Data;
//
//public class BoardRequest {
//
//    @Data
//    // 지금은 (title , content , username) 밖에 없어서 굳이 내부클래스가 필요하지 않지만
//    // (title , username) / (title , content) ... 이런 식으로 Dto를 묶어서 보낼것이라면 내부클래스가 필요.
//
//    public static class SaveDto {
//        private String title;
//        private String content;
//        private String username;
//
//        // 검증 메서드(선택사항)
//        public void validate(){
//            if(title == null || title.trim().isEmpty())throw new IllegalArgumentException("제목은 필수입니다");
//            if(content == null || content.trim().isEmpty()) throw new IllegalArgumentException("내용은 필수입니다");
//            if(username == null || username.trim().isEmpty()) throw new IllegalArgumentException("이름은 필수입니다");
//        }
//
//    }
//
//
//
//    @Data
//    public static class UpdateDto{
//        private String title;
//        private String content;
//
//        // 검증 메서드(선택사항)
//        public void validate(){
//            if(title == null || title.trim().isEmpty())throw new IllegalArgumentException("제목은 필수입니다");
//            if(content == null || content.trim().isEmpty()) throw new IllegalArgumentException("내용은 필수입니다");
//        }
//
//    }
//}
