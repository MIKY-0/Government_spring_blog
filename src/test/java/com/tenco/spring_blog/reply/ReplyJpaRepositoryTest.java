package com.tenco.spring_blog.reply;

import com.tenco.spring_blog.board.Board;
import com.tenco.spring_blog.user.User; // 확인 필요: User 패키지를 추측

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnitUtil;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.sql.init.mode=never",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class ReplyJpaRepositoryTest {

    @Autowired
    private ReplyJpaRepository replyJpaRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private EntityManagerFactory emf;

    // 공통 메서드 1: 테스트용 사용자 저장
    private User createUser() {

        User user = User.builder() // 확인 필요: User에 @Builder가 있다고 추측
                .username("ssar") // 확인 필요: username 빌더 메서드 추측
                .password("1234") // 확인 필요: password 빌더 메서드 추측
                .email("ssar@test.com") // 확인 필요: email 빌더 메서드 추측
                .build();

        em.persist(user);
        return user;
    }

    // 공통 메서드 2: 테스트용 게시글 저장
    private Board createBoard(User user, String title) {

        Board board = Board.builder() // 확인 필요: Board에 @Builder가 있다고 추측
                .title(title) // 확인 필요: title 빌더 메서드 추측
                .content("게시글 내용") // 확인 필요: content 빌더 메서드 추측
                .user(user) // 확인 필요: user 연관관계 빌더 추측
                .build();

        em.persist(board);
        return board;
    }

    // 공통 메서드 3: 테스트용 댓글 저장
    private Reply createReply(
            User user, Board board, String comment) {

        Reply reply = Reply.builder() // 확인 필요: Reply에 @Builder가 있다고 추측
                .comment(comment) // 확인 필요: comment 필드명 추측
                .user(user) // 확인 필요: user 연관관계 빌더 추측
                .board(board) // 확인 필요: board 연관관계 빌더 추측
                .build();

        em.persist(reply);
        return reply;
    }

    // 1. 게시글에 댓글이 존재하는 경우
    @Test
    void findAllByBoardIdJoinUser_댓글존재_테스트() {

        // given: 사용자, 게시글, 댓글을 저장한다.
        User user = createUser();
        Board board = createBoard(user, "게시글1");
        createReply(user, board, "첫 번째 댓글");

        em.flush();
        em.clear();

        // when: 게시글 ID로 댓글 목록을 조회한다.
        List<Reply> result =
                replyJpaRepository.findAllByBoardIdJoinUser(
                        board.getId()
                );

        // then: 댓글이 한 개 조회되는지 확인한다.
        assertThat(result).hasSize(1);
    }

    // 2. 해당 게시글의 댓글만 조회되는지 확인
    @Test
    void findAllByBoardIdJoinUser_다른게시글제외_테스트() {

        // given: 두 게시글에 각각 댓글을 저장한다.
        User user = createUser();

        Board board1 = createBoard(user, "게시글1");
        Board board2 = createBoard(user, "게시글2");

        Reply reply1 = createReply(user, board1, "게시글1 댓글");
        createReply(user, board2, "게시글2 댓글");

        em.flush();
        em.clear();

        // when: 첫 번째 게시글의 댓글만 조회한다.
        List<Reply> result =
                replyJpaRepository.findAllByBoardIdJoinUser(
                        board1.getId()
                );

        // then: 첫 번째 게시글의 댓글 ID만 조회되는지 확인한다.
        assertThat(result)
                .extracting(Reply::getId) // 확인 필요: Reply에 getId()가 있다고 추측
                .containsExactly(reply1.getId());
    }

    // 3. 댓글이 없는 경우
    @Test
    void findAllByBoardIdJoinUser_댓글없음_테스트() {

        // given: 댓글이 없는 게시글을 저장한다.
        User user = createUser();
        Board board = createBoard(user, "게시글1");

        em.flush();
        em.clear();

        // when: 해당 게시글의 댓글 목록을 조회한다.
        List<Reply> result =
                replyJpaRepository.findAllByBoardIdJoinUser(
                        board.getId()
                );

        // then: 빈 리스트인지 확인한다.
        assertThat(result).isEmpty();
    }

    // 4. 댓글 ID 오름차순 정렬
    @Test
    void findAllByBoardIdJoinUser_오름차순_테스트() {

        // given: 동일한 게시글에 댓글 3개를 저장한다.
        User user = createUser();
        Board board = createBoard(user, "게시글1");

        Reply reply1 = createReply(user, board, "댓글1");
        Reply reply2 = createReply(user, board, "댓글2");
        Reply reply3 = createReply(user, board, "댓글3");

        em.flush();
        em.clear();

        // when: 게시글의 댓글 목록을 조회한다.
        List<Reply> result =
                replyJpaRepository.findAllByBoardIdJoinUser(
                        board.getId()
                );

        // then: 댓글 ID가 오름차순으로 정렬되었는지 확인한다.
        assertThat(result)
                .extracting(Reply::getId) // 확인 필요: Reply에 getId()가 있다고 추측
                .containsExactly(
                        reply1.getId(),
                        reply2.getId(),
                        reply3.getId()
                );
    }

    // 5. JOIN FETCH 작성자 로딩 확인
    @Test
    void findAllByBoardIdJoinUser_작성자로딩_테스트() {

        // given: 댓글을 저장하고 영속성 컨텍스트를 비운다.
        User user = createUser();
        Board board = createBoard(user, "게시글1");
        createReply(user, board, "첫 번째 댓글");

        em.flush();
        em.clear();

        // when: JOIN FETCH 메서드로 댓글을 조회한다.
        List<Reply> result =
                replyJpaRepository.findAllByBoardIdJoinUser(
                        board.getId()
                );

        PersistenceUnitUtil util =
                emf.getPersistenceUnitUtil();

        // then: 댓글의 user 연관관계가 로딩되었는지 확인한다.
        assertThat(util.isLoaded(result.get(0), "user"))
                .isTrue();
    }

    // 6. 게시글의 댓글 일괄 삭제
    @Test
    void deleteByBoardId_댓글전체삭제_테스트() {

        // given: 하나의 게시글에 댓글 3개를 저장한다.
        User user = createUser();
        Board board = createBoard(user, "게시글1");

        createReply(user, board, "댓글1");
        createReply(user, board, "댓글2");
        createReply(user, board, "댓글3");

        em.flush();
        em.clear();

        // when: 해당 게시글의 댓글을 일괄 삭제한다.
        replyJpaRepository.deleteByBoardId(board.getId());

        em.clear();

        // then: 해당 게시글의 댓글이 모두 삭제되었는지 확인한다.
        List<Reply> result =
                replyJpaRepository.findAllByBoardIdJoinUser(
                        board.getId()
                );

        assertThat(result).isEmpty();
    }

    // 7. 다른 게시글 댓글은 유지
    @Test
    void deleteByBoardId_다른게시글유지_테스트() {

        // given: 서로 다른 게시글에 댓글을 저장한다.
        User user = createUser();

        Board board1 = createBoard(user, "게시글1");
        Board board2 = createBoard(user, "게시글2");

        createReply(user, board1, "삭제할 댓글");
        createReply(user, board2, "유지할 댓글");

        em.flush();
        em.clear();

        // when: 첫 번째 게시글의 댓글만 삭제한다.
        replyJpaRepository.deleteByBoardId(board1.getId());

        em.clear();

        // then: 두 번째 게시글의 댓글은 유지되는지 확인한다.
        List<Reply> result =
                replyJpaRepository.findAllByBoardIdJoinUser(
                        board2.getId()
                );

        assertThat(result).hasSize(1);
    }

    // 8. 삭제할 댓글이 없는 경우
    @Test
    void deleteByBoardId_댓글없음_테스트() {

        // given: 댓글이 없는 게시글을 저장한다.
        User user = createUser();
        Board board = createBoard(user, "게시글1");

        em.flush();
        em.clear();

        // when: 댓글이 없는 게시글에 대해 삭제를 실행한다.
        replyJpaRepository.deleteByBoardId(board.getId());

        // then: 예외 없이 실행되고 댓글 개수가 0인지 확인한다.
        assertThat(
                replyJpaRepository
                        .findAllByBoardIdJoinUser(board.getId())
        ).isEmpty();
    }

    // 9. 삭제 후 DB 재조회
    @Test
    void deleteByBoardId_삭제후재조회_테스트() {

        // given: 댓글을 저장하고 ID를 보관한다.
        User user = createUser();
        Board board = createBoard(user, "게시글1");
        Reply reply = createReply(user, board, "삭제할 댓글");

        em.flush();
        Long replyId = reply.getId(); // 확인 필요: Reply에 getId()가 있다고 추측
        em.clear();

        // when: 게시글의 댓글을 삭제한다.
        replyJpaRepository.deleteByBoardId(board.getId());

        em.clear();

        // then: 삭제한 댓글 ID로 조회해도 데이터가 없는지 확인한다.
        assertThat(replyJpaRepository.findById(replyId))
                .isEmpty();
    }
}