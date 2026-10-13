package com.tenco.spring_blog.board;

import com.tenco.spring_blog._core.util.MyDateUtil;
import lombok.Data;

/*
게시글 응답 DTO
서비스(트랜잭션 안)에서 엔티티를 DTO로 변환해서 반환.
뷰는 엔티티가 아닌 DTO만 사용하므로 OSIV를 꺼도 지연 로딩 오류가 나지 않음.
화면에 필요한 값만 담아 전달하므로 계층간 결합도도 낮아짐.
 */
public class BoardResponse {

    @Data
    public static class ListDto{
        private Long id;
        private String title;
        private String username;
        private String createdAt;

        public ListDto(Board board) {
            this.id = board.getId();
            this.title = board.getTitle();
            this.createdAt = board.getTime();

            // join fetch로 이미 가져온 user를 사용할 예정.(추가 쿼리 발생 없음)
            if(board.getUser() != null){
                this.username = board.getUser().getUsername();
            }
        }
    }


    // 게시글 상세 페이지 응답 DTO.
    @Data
    public static class DetailDto{
        private Long id;
        private String title;
        private String content;
        private Long userId;
        private String username;
        private String createdAt;

        public DetailDto(Board board){
            this.id = board.getId();
            this.title = board.getTitle();
            this.content = board.getContent();

            // 지연 로딩 오류 방지를 위한 방어적 코드 작성.
            if(board.getUser() != null){
                this.userId = board.getUser().getId();
                this.username = board.getUser().getUsername();
            }
            if (board.getCreatedAt() != null){
                this.createdAt = MyDateUtil.timestampFormat(board.getCreatedAt());
            }
        }
    }


    // 게시글 수정 화면 DTO.
    @Data
    public static class UpdateFormDto{
        private Long id;
        private String title;
        private String content;

        public UpdateFormDto(Board board) {
            this.id = board.getId();
            this.title = board.getTitle();
            this.content = board.getTitle();
        }
    }

}
