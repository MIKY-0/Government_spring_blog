//package com.tenco.spring_blog.board2;
//
//import com.sun.nio.sctp.IllegalReceiveException;
//import lombok.Data;
//
//public class BoardRequest2 {
//
//    @Data
//    public static class SaveDto {
//        private String username;
//        private String title;
//        private String content;
//
//        public void validateSave() {
//            boolean isNull = (username == null || username.trim().isEmpty() ||
//                              title == null || title.trim().isEmpty() ||
//                              content == null || content.trim().isEmpty());
//
//            if(isNull) throw new IllegalReceiveException("이름 , 제목 , 내용은 비어있을수 없습니다.");
//        }
//    }
//
//
//
//    @Data
//    public static class UpdateDto {
//        private String title;
//        private String content;
//
//        public void validateUpdate() {
//            boolean isNull = (title == null || title.trim().isEmpty() ||
//                             content == null || content.trim().isEmpty());
//
//            if(isNull) throw new IllegalReceiveException("이름 , 제목 , 내용은 비어있을수 없습니다.");
//        }
//    }
//}
