package com.tenco.spring_blog.board2;

import lombok.Builder;
import lombok.Data;

public class BoardRequest2 {

    @Data
    public static class SaveDto{
        private String username;
        private String title;
        private String content;
    }

    @Data
    public static class UpdateDto{
        private String title;
        private String content;
    }


}
