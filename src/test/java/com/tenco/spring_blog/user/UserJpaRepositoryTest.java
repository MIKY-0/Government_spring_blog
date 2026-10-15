//
//package com.tenco.spring_blog.user;
//
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
//class UserJpaRepositoryTest {
//
//    @Autowired
//    private UserJpaRepository userJpaRepository;
//
//    // 테스트 회원 생성
//    private User createUser(String username, String email) {
//        return User.builder()
//                .username(username)
//                .password("1234")
//                .email(email)
//                .build();
//    }
//
//    @Test
//    @DisplayName("회원 저장 테스트")
//    void save_test() {
//        // given
//        User user = createUser("ssar", "ssar@test.com");
//
//        // when
//        User savedUser = userJpaRepository.save(user);
//
//        // then
//        assertNotNull(savedUser.getId());
//        assertEquals("ssar", savedUser.getUsername());
//        assertEquals("ssar@test.com", savedUser.getEmail());
//    }
//
//    @Test
//    @DisplayName("ID로 회원 조회")
//    void findById_test() {
//        // given
//        User savedUser = userJpaRepository.save(
//                createUser("ssar", "ssar@test.com")
//        );
//
//        // when
//        Optional<User> result =
//                userJpaRepository.findById(savedUser.getId());
//
//        // then
//        assertTrue(result.isPresent());
//        assertEquals("ssar", result.get().getUsername());
//    }
//
//    @Test
//    @DisplayName("전체 회원 조회")
//    void findAll_test() {
//        // given
//        userJpaRepository.save(createUser("ssar", "ssar@test.com"));
//        userJpaRepository.save(createUser("cos", "cos@test.com"));
//
//        // when
//        List<User> users = userJpaRepository.findAll();
//
//        // then
//        assertTrue(users.stream()
//                .anyMatch(u -> u.getUsername().equals("ssar")));
//        assertTrue(users.stream()
//                .anyMatch(u -> u.getUsername().equals("cos")));
//    }
//
//    @Test
//    @DisplayName("사용자명으로 회원 조회")
//    void findByUsername_test() {
//        // given
//        userJpaRepository.save(
//                createUser("ssar", "ssar@test.com")
//        );
//
//        // when
//        Optional<User> result =
//                userJpaRepository.findByUsername("ssar");
//
//        // then
//        assertTrue(result.isPresent());
//        assertEquals("ssar@test.com", result.get().getEmail());
//    }
//
//    @Test
//    @DisplayName("이메일로 회원 조회")
//    void findByEmail_test() {
//        // given
//        userJpaRepository.save(
//                createUser("ssar", "ssar@test.com")
//        );
//
//        // when
//        Optional<User> result =
//                userJpaRepository.findByEmail("ssar@test.com");
//
//        // then
//        assertTrue(result.isPresent());
//        assertEquals("ssar", result.get().getUsername());
//    }
//
//    @Test
//    @DisplayName("로그인 정보 조회 성공")
//    void findByUsernameAndPassword_success_test() {
//        // given
//        userJpaRepository.save(
//                createUser("ssar", "ssar@test.com")
//        );
//
//        // when
//        Optional<User> result =
//                userJpaRepository.findByUsernameAndPassword("ssar", "1234");
//
//        // then
//        assertTrue(result.isPresent());
//        assertEquals("ssar", result.get().getUsername());
//    }
//
//    @Test
//    @DisplayName("로그인 정보 조회 실패")
//    void findByUsernameAndPassword_fail_test() {
//        // given
//        userJpaRepository.save(
//                createUser("ssar", "ssar@test.com")
//        );
//
//        // when
//        Optional<User> result =
//                userJpaRepository.findByUsernameAndPassword("ssar", "wrong");
//
//        // then
//        assertTrue(result.isEmpty());
//    }
//
//    @Test
//    @DisplayName("회원 존재 여부 확인")
//    void existsById_test() {
//        // given
//        User savedUser = userJpaRepository.save(
//                createUser("ssar", "ssar@test.com")
//        );
//
//        // when
//        boolean exists =
//                userJpaRepository.existsById(savedUser.getId());
//
//        // then
//        assertTrue(exists);
//    }
//
//    @Test
//    @DisplayName("회원 삭제")
//    void deleteById_test() {
//        // given
//        User savedUser = userJpaRepository.save(
//                createUser("ssar", "ssar@test.com")
//        );
//
//        // when
//        userJpaRepository.deleteById(savedUser.getId());
//
//        // then
//        assertFalse(userJpaRepository.existsById(savedUser.getId()));
//    }
//}
