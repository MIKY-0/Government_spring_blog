package com.tenco.spring_blog.reply;

import com.tenco.spring_blog._core.util.MyDateUtil;
import lombok.Data;

public class ReplyResponse {
    // 댓글 목록 응답 Dto.
    @Data
    public static class ListDto{
        private Long id;
        private String username;
        private String comment;
        private String createdAt;
        private boolean myComment;

        public ListDto(Reply reply , Long sessionUserId) {
            this.id = reply.getId();
            this.comment = reply.getComment();
            this.username = reply.getUser().getUsername();
            this.myComment = reply.isOwner(sessionUserId);

            if(reply.getCreatedAt() != null){
                this.createdAt = MyDateUtil.timestampFormat(reply.getCreatedAt());
            }

        }
    }
}
