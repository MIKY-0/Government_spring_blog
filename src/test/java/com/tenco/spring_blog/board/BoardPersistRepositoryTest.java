package com.tenco.spring_blog.board;

import com.tenco.spring_blog.user.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

@Import(BoardJpaRepository.class)   @DataJpaTest
public class BoardPersistRepositoryTest {
    @Autowired
    private BoardJpaRepository boardJpaRepository;

    @Test
    public void save_연관관계_포함_게시글_저장_테스트() {
        // given
        // 1. User 객체 생성. 실제로는 세션에서 가져와야함.
        User user = new User(1L , "testuser" ,  "test@email.com" , null  , "1234");
        Board board = Board.builder()
                .title("테스트")
                .content("테스트 내용")
                .user(user)
                .build();

        // when
        Board savedBoard = boardJpaRepository.save(board);

        // then
        // 1. 자동 생성된 ID값 확인.
        Assertions.assertThat(savedBoard.getId()).isNotNull();
        Assertions.assertThat(savedBoard.getId()).isGreaterThan(0);

        // 2. 입력한 데이터가 올바르게 저장되었는지 확인.
        Assertions.assertThat(savedBoard.getTitle()).isEqualTo("테스트");
        Assertions.assertThat(savedBoard.getContent()).isEqualTo("테스트 내용");

        // 3. 연관관계가 올바르게 저장되었는지 확인.
        Assertions.assertThat(savedBoard.getUser()).isNotNull();
        Assertions.assertThat(savedBoard.getUser().getUsername()).isEqualTo("testuser");

        // 4. 원본 객체와 반환된 객체가 동일한 참조인지 확인.
        Assertions.assertThat(board).isSameAs(savedBoard);
    }

    @Test
    public void delete_게시글_삭제_테스트() {

        // given
        // 1. 게시글 저장을 위한 User 객체 생성
        User user = new User(
                1L,
                "testuser",
                "a@naver.com",
                null,
                "1234"
        );

        // 2. 삭제할 게시글 객체 생성
        Board board = Board.builder()
                .title("삭제할 게시글")
                .content("삭제할 내용")
                .user(user)
                .build();

        // 3. 게시글 저장
        Board savedBoard = boardJpaRepository.save(board);

        // 4. 저장된 게시글의 기본키(ID) 가져오기
        Long boardId = savedBoard.getId();


        // when
        // 5. 게시글 삭제
        boardJpaRepository.deleteById(boardId);

        // then
        // 6. 삭제된 게시글을 다시 조회
        Board deletedBoard = boardJpaRepository.findById(boardId);

        // 7. 삭제된 게시글은 조회되지 않아야 함
        Assertions.assertThat(deletedBoard).isNull();

    }

}
