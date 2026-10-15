//package com.tenco.spring_blog.reply;
//
//import com.tenco.spring_blog.board.Board;
//import com.tenco.spring_blog.user.User;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
//import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
//
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@DataJpaTest
//class ReplyJpaRepositoryTest {
//
//    @Autowired
//    private ReplyJpaRepository replyJpaRepository;
//
//    @Autowired
//    private TestEntityManager em;
//
//    @Test
//    @DisplayName("게시글 댓글 목록 조회 - User Fetch Join 및 오래된 순 정렬")
//    void findAllByBoardIdJoinUser_test() {
//
//        // given
//        // 1. 사용자 생성
//        User user = User.builder()
//                .username("testUser")
//                .password("1234")
//                .email("test@test.com")
//                .build();
//
//        em.persist(user);
//
//        // 2. 게시글 생성
//        Board board = Board.builder()
//                .title("테스트 게시글")
//                .content("테스트 내용")
//                .user(user)
//                .build();
//
//        em.persist(board);
//
//        // 3. 댓글 생성
//        Reply reply1 = Reply.builder()
//                .comment("첫 번째 댓글")
//                .board(board)
//                .user(user)
//                .build();
//
//        Reply reply2 = Reply.builder()
//                .comment("두 번째 댓글")
//                .board(board)
//                .user(user)
//                .build();
//
//        em.persist(reply1);
//        em.persist(reply2);
//
//        // INSERT SQL 실행 및 영속성 컨텍스트 초기화
//        em.flush();
//        em.clear();
//
//        // when
//        List<Reply> replies =
//                replyJpaRepository.findAllByBoardIdJoinUser(board.getId());
//
//        // then
//        assertThat(replies).hasSize(2);
//
//        // 오래된 순 정렬 확인
//        assertThat(replies.get(0).getId()).isEqualTo(reply1.getId());
//        assertThat(replies.get(1).getId()).isEqualTo(reply2.getId());
//
//        // 댓글 내용 확인
//        assertThat(replies.get(0).getComment()).isEqualTo("첫 번째 댓글");
//        assertThat(replies.get(1).getComment()).isEqualTo("두 번째 댓글");
//
//        // Fetch Join 확인
////        assertThat(
////                em.getEntityManagerFac()
////                        .getPersistenceUnitUtil()
////                        .isLoaded(replies.get(0).getUser())
////        ).isTrue();
//    }
//}