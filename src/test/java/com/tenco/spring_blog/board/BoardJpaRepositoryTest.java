package com.tenco.spring_blog.board;

import com.tenco.spring_blog.user.User; // 확인 필요: User의 패키지를 추측

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnitUtil;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.sql.init.mode=never",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class BoardJpaRepositoryTest {

    @Autowired
    private BoardJpaRepository boardJpaRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private EntityManagerFactory emf;

    // 공통 메서드 1: 테스트용 사용자 저장
    private User createUser() {
        User user = User.builder() // 확인 필요: User에 @Builder가 있다고 추측
                .username("ssar") // 확인 필요: username 필드 추측
                .password("1234") // 확인 필요: password 필드 추측
                .email("ssar@test.com") // 확인 필요: email 필드 추측
                .build();

        em.persist(user);
        return user;
    }

    // 공통 메서드 2: 테스트용 게시글 저장
    private Board createBoard(
            User user, String title, String content) {

        Board board = Board.builder() // 확인 필요: Board에 @Builder가 있다고 추측
                .title(title) // 확인 필요: title 빌더 메서드 추측
                .content(content) // 확인 필요: content 빌더 메서드 추측
                .user(user) // 확인 필요: Board가 User를 참조한다고 추측
                .build();

        return boardJpaRepository.saveAndFlush(board);
    }

    // 공통 메서드 3: 게시글 여러 개 저장
    private List<Board> createBoards(User user) {
        List<Board> boards = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            boards.add(createBoard(
                    user, "제목" + i, "내용" + i
            ));
        }

        return boards;
    }

    // 1. ID로 게시글 조회 성공
    @Test
    void findByIdJoinUser_게시글존재_테스트() {

        // given: 사용자와 게시글을 저장한다.
        User user = createUser();
        Board board = createBoard(user, "제목1", "내용1");
        em.clear();

        // when: 저장된 게시글 ID로 조회한다.
        Optional<Board> result =
                boardJpaRepository.findByIdJoinUser(board.getId());

        // then: 조회된 게시글 ID가 일치하는지 확인한다.
        assertThat(result.orElseThrow().getId())
                .isEqualTo(board.getId());
    }

    // 2. 존재하지 않는 게시글 조회
    @Test
    void findByIdJoinUser_게시글없음_테스트() {

        // given: 게시글을 저장하지 않는다.

        // when: 존재하지 않는 ID로 조회한다.
        Optional<Board> result =
                boardJpaRepository.findByIdJoinUser(-1L);

        // then: 조회 결과가 비어 있는지 확인한다.
        assertThat(result).isEmpty();
    }

    // 3. JOIN FETCH 로딩 확인
    @Test
    void findByIdJoinUser_작성자로딩_테스트() {

        // given: 사용자와 게시글을 저장한 뒤 영속성 컨텍스트를 비운다.
        User user = createUser();
        Board board = createBoard(user, "제목1", "내용1");
        em.clear();

        // when: JOIN FETCH 메서드로 게시글을 조회한다.
        Board result = boardJpaRepository
                .findByIdJoinUser(board.getId())
                .orElseThrow();

        PersistenceUnitUtil util =
                emf.getPersistenceUnitUtil();

        // then: user 연관관계가 이미 로딩되었는지 확인한다.
        assertThat(util.isLoaded(result, "user"))
                .isTrue();
    }

    // 4. 페이지 크기 검증
    @Test
    void findAllJoinUser_페이지크기_테스트() {

        // given: 게시글 5개와 페이지 크기 2를 준비한다.
        User user = createUser();
        createBoards(user);

        Pageable pageable = PageRequest.of(0, 2);

        // when: 첫 번째 페이지를 조회한다.
        Page<Board> result =
                boardJpaRepository.findAllJoinUser(pageable);

        // then: 첫 페이지에 게시글 2개가 있는지 확인한다.
        assertThat(result.getContent()).hasSize(2);
    }

    // 5. 전체 게시글 개수 검증
    @Test
    void findAllJoinUser_전체개수_테스트() {

        // given: 게시글 5개를 저장한다.
        User user = createUser();
        createBoards(user);

        Pageable pageable = PageRequest.of(0, 2);

        // when: 페이징 조회를 실행한다.
        Page<Board> result =
                boardJpaRepository.findAllJoinUser(pageable);

        // then: 전체 게시글 개수가 5개인지 확인한다.
        assertThat(result.getTotalElements()).isEqualTo(5);
    }

    // 6. 전체 페이지 수 검증
    @Test
    void findAllJoinUser_전체페이지수_테스트() {

        // given: 게시글 5개, 페이지 크기 2를 준비한다.
        User user = createUser();
        createBoards(user);

        Pageable pageable = PageRequest.of(0, 2);

        // when: 페이징 조회를 실행한다.
        Page<Board> result =
                boardJpaRepository.findAllJoinUser(pageable);

        // then: 전체 페이지가 3개인지 확인한다.
        assertThat(result.getTotalPages()).isEqualTo(3);
    }

    // 7. ID 내림차순 정렬 검증
    @Test
    void findAllJoinUser_내림차순_테스트() {

        // given: 게시글을 순서대로 저장한다.
        User user = createUser();
        List<Board> boards = createBoards(user);

        Pageable pageable = PageRequest.of(
                0, 5, Sort.by(Sort.Direction.DESC, "id")
        );

        // when: ID 내림차순으로 조회한다.
        Page<Board> result =
                boardJpaRepository.findAllJoinUser(pageable);

        // then: 가장 나중에 저장한 게시글이 첫 번째인지 확인한다.
        assertThat(result.getContent().get(0).getId())
                .isEqualTo(boards.get(4).getId());
    }

    // 8. 두 번째 페이지 조회
    @Test
    void findAllJoinUser_두번째페이지_테스트() {

        // given: 게시글 5개를 저장하고 ID 내림차순으로 설정한다.
        User user = createUser();
        List<Board> boards = createBoards(user);

        Pageable pageable = PageRequest.of(
                1, 2, Sort.by(Sort.Direction.DESC, "id")
        );

        // when: 두 번째 페이지를 조회한다.
        Page<Board> result =
                boardJpaRepository.findAllJoinUser(pageable);

        // then: 두 번째 페이지의 첫 게시글이 기대한 ID인지 확인한다.
        assertThat(result.getContent().get(0).getId())
                .isEqualTo(boards.get(2).getId());
    }

    // 9. 페이징 조회 JOIN FETCH 검증
    @Test
    void findAllJoinUser_작성자로딩_테스트() {

        // given: 사용자와 게시글을 저장하고 영속성 컨텍스트를 비운다.
        User user = createUser();
        createBoard(user, "제목1", "내용1");
        em.clear();

        Pageable pageable = PageRequest.of(0, 2);

        // when: 작성자를 함께 조회하는 페이징 메서드를 실행한다.
        Page<Board> result =
                boardJpaRepository.findAllJoinUser(pageable);

        PersistenceUnitUtil util =
                emf.getPersistenceUnitUtil();

        // then: 조회된 게시글의 user가 로딩되었는지 확인한다.
        assertThat(util.isLoaded(
                result.getContent().get(0), "user"
        )).isTrue();
    }

    // 10. 게시글이 없는 경우
    @Test
    void findAllJoinUser_게시글없음_테스트() {

        // given: 게시글을 저장하지 않는다.
        Pageable pageable = PageRequest.of(0, 2);

        // when: 전체 게시글을 조회한다.
        Page<Board> result =
                boardJpaRepository.findAllJoinUser(pageable);

        // then: 페이지 내용이 비어 있는지 확인한다.
        assertThat(result.getContent()).isEmpty();
    }

    // 11. 제목만 검색어와 일치
    @Test
    void findAllKeywordJoinUser_제목일치_테스트() {

        // given: 제목에만 검색어가 포함된 게시글을 저장한다.
        User user = createUser();
        createBoard(user, "스프링 공부", "자바 내용");

        Pageable pageable = PageRequest.of(0, 10);

        // when: 스프링이라는 키워드로 검색한다.
        Page<Board> result =
                boardJpaRepository.findAllKeywordJoinUser(
                        "스프링", pageable
                );

        // then: 검색된 게시글 제목이 일치하는지 확인한다.
        assertThat(result.getContent().get(0).getTitle())
                .isEqualTo("스프링 공부");
    }

    // 12. 내용만 검색어와 일치
    @Test
    void findAllKeywordJoinUser_내용일치_테스트() {

        // given: 내용에만 검색어가 포함된 게시글을 저장한다.
        User user = createUser();
        createBoard(user, "자바 공부", "스프링 내용");

        Pageable pageable = PageRequest.of(0, 10);

        // when: 스프링이라는 키워드로 검색한다.
        Page<Board> result =
                boardJpaRepository.findAllKeywordJoinUser(
                        "스프링", pageable
                );

        // then: 검색된 게시글 내용이 일치하는지 확인한다.
        assertThat(result.getContent().get(0).getContent())
                .isEqualTo("스프링 내용");
    }

    // 13. 제목과 내용 모두 불일치
    @Test
    void findAllKeywordJoinUser_모두불일치_테스트() {

        // given: 검색어가 없는 게시글을 저장한다.
        User user = createUser();
        createBoard(user, "자바 공부", "객체지향 내용");

        Pageable pageable = PageRequest.of(0, 10);

        // when: 스프링이라는 키워드로 검색한다.
        Page<Board> result =
                boardJpaRepository.findAllKeywordJoinUser(
                        "스프링", pageable
                );

        // then: 검색 결과가 없는지 확인한다.
        assertThat(result.getContent()).isEmpty();
    }

    // 14. 빈 키워드 검색
    @Test
    void findAllKeywordJoinUser_빈키워드_테스트() {

        // given: 서로 다른 게시글 3개를 저장한다.
        User user = createUser();
        createBoard(user, "자바", "내용1");
        createBoard(user, "스프링", "내용2");
        createBoard(user, "JPA", "내용3");

        Pageable pageable = PageRequest.of(0, 10);

        // when: 빈 문자열로 검색한다.
        Page<Board> result =
                boardJpaRepository.findAllKeywordJoinUser(
                        "", pageable
                );

        // then: 모든 게시글 3개가 검색되는지 확인한다.
        assertThat(result.getTotalElements()).isEqualTo(3);
    }

    // 15. 검색 결과 페이징
    @Test
    void findAllKeywordJoinUser_검색페이징_테스트() {

        // given: 스프링 게시글 3개와 다른 게시글 1개를 저장한다.
        User user = createUser();
        createBoard(user, "스프링1", "내용1");
        createBoard(user, "스프링2", "내용2");
        createBoard(user, "스프링3", "내용3");
        createBoard(user, "자바", "내용4");

        Pageable pageable = PageRequest.of(0, 2);

        // when: 스프링 키워드로 검색한다.
        Page<Board> result =
                boardJpaRepository.findAllKeywordJoinUser(
                        "스프링", pageable
                );

        // then: 검색 결과가 총 2페이지인지 확인한다.
        assertThat(result.getTotalPages()).isEqualTo(2);
    }
}