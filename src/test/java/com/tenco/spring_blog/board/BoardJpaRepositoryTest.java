//
//package com.tenco.spring_blog.board;
//
//import com.tenco.spring_blog.user.User;
//import com.tenco.spring_blog.user.UserJpaRepository;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;
//import org.hibernate.Hibernate;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@DataJpaTest
//class BoardJpaRepositoryTest {
//
//    @Autowired
//    private BoardJpaRepository boardJpaRepository;
//
//    @Autowired
//    private UserJpaRepository userJpaRepository;
//
//    @PersistenceContext
//    private EntityManager em;
//
//    // 테스트용 작성자 저장
//    private User createUser(String username, String email) {
//        User user = User.builder()
//                .username(username)
//                .password("1234")
//                .email(email)
//                .build();
//
//        return userJpaRepository.save(user);
//    }
//
//    // 테스트용 게시글 저장
//    private Board createBoard(String title, User user) {
//        Board board = Board.builder()
//                .title(title)
//                .content("테스트 내용")
//                .user(user)
//                .build();
//
//        return boardJpaRepository.save(board);
//    }
//
//    @Test
//    @DisplayName("게시글 저장 성공")
//    void save_test() {
//        // given
//        User user = createUser("ssar", "ssar@test.com");
//
//        // when
//        Board board = createBoard("첫 게시글", user);
//
//        // then
//        assertNotNull(board.getId());
//        assertEquals("첫 게시글", board.getTitle());
//        assertEquals(user.getId(), board.getUser().getId());
//    }
//
//    @Test
//    @DisplayName("게시글 단건 조회 성공")
//    void findById_test() {
//        // given
//        User user = createUser("ssar", "ssar@test.com");
//        Board board = createBoard("첫 게시글", user);
//
//        em.flush();
//        em.clear();
//
//        // when
//        Optional<Board> result =
//                boardJpaRepository.findById(board.getId());
//
//        // then
//        assertTrue(result.isPresent());
//        assertEquals("첫 게시글", result.get().getTitle());
//    }
//
//    @Test
//    @DisplayName("게시글 전체 조회")
//    void findAll_test() {
//        // given
//        User user = createUser("ssar", "ssar@test.com");
//
//        Board board1 = createBoard("게시글1", user);
//        Board board2 = createBoard("게시글2", user);
//
//        em.flush();
//        em.clear();
//
//        // when
//        List<Board> result = boardJpaRepository.findAll();
//
//        // then
//        assertTrue(result.stream()
//                .anyMatch(b -> b.getId().equals(board1.getId())));
//        assertTrue(result.stream()
//                .anyMatch(b -> b.getId().equals(board2.getId())));
//    }
//
//    @Test
//    @DisplayName("JOIN FETCH 단건 조회")
//    void findByIdJoinUser_test() {
//        // given
//        User user = createUser("ssar", "ssar@test.com");
//        Board board = createBoard("첫 게시글", user);
//
//        em.flush();
//        em.clear();
//
//        // when
//        Board result = boardJpaRepository
//                .findByIdJoinUser(board.getId())
//                .orElseThrow();
//
//        // then
//        assertEquals("첫 게시글", result.getTitle());
//        assertEquals("ssar", result.getUser().getUsername());
//        assertTrue(Hibernate.isInitialized(result.getUser()));
//    }
//
//    @Test
//    @DisplayName("JOIN FETCH 전체 조회 및 최신순 정렬")
//    void findAllJoinUser_test() {
//        // given
//        User user1 = createUser("ssar", "ssar@test.com");
//        User user2 = createUser("cos", "cos@test.com");
//
//        Board board1 = createBoard("게시글1", user1);
//        Board board2 = createBoard("게시글2", user2);
//
//        em.flush();
//        em.clear();
//
//        // when
//        List<Board> result = boardJpaRepository.findAllJoinUser();
//
//// 내가 저장한 게시글이 포함되어 있는지 확인.  then
//        assertTrue(result.stream()
//                .anyMatch(b -> b.getId().equals(board1.getId())));
//
//        assertTrue(result.stream()
//                .anyMatch(b -> b.getId().equals(board2.getId())));
//
//// 전체 게시글이 ID 내림차순인지 확인
//        for (int i = 0; i < result.size() - 1; i++) {
//            assertTrue(result.get(i).getId() > result.get(i + 1).getId());
//        }
//
//// JOIN FETCH 확인
//        for (Board board : result) {
//            assertTrue(Hibernate.isInitialized(board.getUser()));
//        }
//    }
//
//    @Test
//    @DisplayName("게시글 삭제")
//    void deleteById_test() {
//        // given
//        User user = createUser("ssar", "ssar@test.com");
//        Board board = createBoard("삭제할 게시글", user);
//
//        Long boardId = board.getId();
//
//        // when
//        boardJpaRepository.deleteById(boardId);
//
//        em.flush();
//        em.clear();
//
//        // then
//        assertFalse(boardJpaRepository.existsById(boardId));
//    }
//}
