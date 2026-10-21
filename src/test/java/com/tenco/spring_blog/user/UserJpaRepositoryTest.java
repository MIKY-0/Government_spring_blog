package com.tenco.spring_blog.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnitUtil;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.sql.init.mode=never",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class UserJpaRepositoryTest {

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private EntityManagerFactory emf;

    // 테스트용 사용자 생성
    private User createUser(String username, String email) {
        return User.builder() // 확인 필요: User에 builder()가 있다고 추측
                .username(username) // 확인 필요: username 빌더 메서드가 있다고 추측
                .password("encodedPassword") // 확인 필요: password 빌더 메서드가 있다고 추측
                .email(email) // 확인 필요: email 빌더 메서드가 있다고 추측
                .build();
    }

    // 테스트용 사용자 생성 및 권한 추가
    private User createUserWithRole(String username, String email) {
        User user = createUser(username, email);
        user.addRole(Role.USER); // 확인 필요: Role.USER와 addRole()은 이전에 제시한 회원가입 코드 기준
        return user;
    }

    // 1. 사용자명과 비밀번호 모두 일치
    @Test
    void findByUsernameAndPassword_모두일치_테스트() {

        // given: 사용자명과 비밀번호가 있는 사용자를 저장한다.
        User user = createUser("ssar", "ssar@test.com");
        userJpaRepository.saveAndFlush(user);

        // when: 올바른 사용자명과 비밀번호로 조회한다.
        Optional<User> result =
                userJpaRepository.findByUsernameAndPassword(
                        "ssar", "encodedPassword"
                );

        // then: 사용자가 조회되는지 확인한다.
        assertThat(result).isPresent();
    }

    // 2. 비밀번호 불일치
    @Test
    void findByUsernameAndPassword_비밀번호불일치_테스트() {

        // given: 사용자 한 명을 저장한다.
        User user = createUser("ssar", "ssar@test.com");
        userJpaRepository.saveAndFlush(user);

        // when: 잘못된 비밀번호로 조회한다.
        Optional<User> result =
                userJpaRepository.findByUsernameAndPassword(
                        "ssar", "wrongPassword"
                );

        // then: 조회 결과가 비어 있는지 확인한다.
        assertThat(result).isEmpty();
    }

    // 3. 존재하지 않는 사용자
    @Test
    void findByUsernameAndPassword_사용자없음_테스트() {

        // given: 사용자를 저장하지 않는다.

        // when: 존재하지 않는 사용자명과 비밀번호로 조회한다.
        Optional<User> result =
                userJpaRepository.findByUsernameAndPassword(
                        "unknown", "1234"
                );

        // then: 조회 결과가 비어 있는지 확인한다.
        assertThat(result).isEmpty();
    }

    // 4. 사용자명 일치
    @Test
    void findByUsername_일치_테스트() {

        // given: 사용자 한 명을 저장한다.
        User user = createUser("ssar", "ssar@test.com");
        userJpaRepository.saveAndFlush(user);

        // when: 저장한 사용자명으로 조회한다.
        Optional<User> result =
                userJpaRepository.findByUsername("ssar");

        // then: 조회된 사용자의 이름이 일치하는지 확인한다.
        assertThat(result.orElseThrow().getUsername())
                .isEqualTo("ssar");
    }

    // 5. 사용자명 불일치
    @Test
    void findByUsername_불일치_테스트() {

        // given: ssar 사용자를 저장한다.
        User user = createUser("ssar", "ssar@test.com");
        userJpaRepository.saveAndFlush(user);

        // when: 다른 사용자명으로 조회한다.
        Optional<User> result =
                userJpaRepository.findByUsername("cos");

        // then: 조회 결과가 비어 있는지 확인한다.
        assertThat(result).isEmpty();
    }

    // 6. 사용자 없음
    @Test
    void findByUsername_사용자없음_테스트() {

        // given: 사용자를 저장하지 않는다.

        // when: 존재하지 않는 사용자명으로 조회한다.
        Optional<User> result =
                userJpaRepository.findByUsername("unknown");

        // then: 조회 결과가 비어 있는지 확인한다.
        assertThat(result).isEmpty();
    }

    // 7. JOIN FETCH 사용자 조회
    @Test
    void findByUsernameWithRoles_일치_테스트() {

        // given: USER 권한이 있는 사용자를 저장한다.
        User user = createUserWithRole("ssar", "ssar@test.com");
        userJpaRepository.saveAndFlush(user);
        em.clear();

        // when: 사용자명으로 권한까지 함께 조회한다.
        Optional<User> result =
                userJpaRepository.findByUsernameWithRoles("ssar");

        // then: 해당 사용자가 조회되는지 확인한다.
        assertThat(result).isPresent();
    }

    // 8. JOIN FETCH 연관관계 로딩 검증
    @Test
    void findByUsernameWithRoles_권한로딩_테스트() {

        // given: 권한이 있는 사용자를 저장한 뒤 영속성 컨텍스트를 비운다.
        User user = createUserWithRole("ssar", "ssar@test.com");
        userJpaRepository.saveAndFlush(user);
        em.clear();

        // when: JOIN FETCH 메서드로 사용자를 조회한다.
        User result = userJpaRepository
                .findByUsernameWithRoles("ssar")
                .orElseThrow();

        PersistenceUnitUtil util =
                emf.getPersistenceUnitUtil();

        // then: roles 연관관계가 이미 로딩되었는지 확인한다.
        assertThat(util.isLoaded(result, "roles"))
                .isTrue();
    }

    // 9. JOIN FETCH 사용자명 불일치
    @Test
    void findByUsernameWithRoles_불일치_테스트() {

        // given: USER 권한이 있는 사용자를 저장한다.
        User user = createUserWithRole("ssar", "ssar@test.com");
        userJpaRepository.saveAndFlush(user);
        em.clear();

        // when: 다른 사용자명으로 조회한다.
        Optional<User> result =
                userJpaRepository.findByUsernameWithRoles("cos");

        // then: 조회 결과가 비어 있는지 확인한다.
        assertThat(result).isEmpty();
    }

    // 10. JOIN FETCH 사용자 없음
    @Test
    void findByUsernameWithRoles_사용자없음_테스트() {

        // given: 사용자를 저장하지 않는다.

        // when: 존재하지 않는 사용자명으로 조회한다.
        Optional<User> result =
                userJpaRepository.findByUsernameWithRoles("unknown");

        // then: 조회 결과가 비어 있는지 확인한다.
        assertThat(result).isEmpty();
    }

    // 11. 이메일 일치
    @Test
    void findByEmail_일치_테스트() {

        // given: 이메일이 있는 사용자를 저장한다.
        User user = createUser("ssar", "ssar@test.com");
        userJpaRepository.saveAndFlush(user);

        // when: 저장된 이메일로 조회한다.
        Optional<User> result =
                userJpaRepository.findByEmail("ssar@test.com");

        // then: 조회된 사용자의 이메일이 일치하는지 확인한다.
        assertThat(result.orElseThrow().getEmail())
                .isEqualTo("ssar@test.com");
    }

    // 12. 이메일 불일치
    @Test
    void findByEmail_불일치_테스트() {

        // given: 사용자 한 명을 저장한다.
        User user = createUser("ssar", "ssar@test.com");
        userJpaRepository.saveAndFlush(user);

        // when: 다른 이메일로 조회한다.
        Optional<User> result =
                userJpaRepository.findByEmail("cos@test.com");

        // then: 조회 결과가 비어 있는지 확인한다.
        assertThat(result).isEmpty();
    }

    // 13. 이메일 사용자 없음
    @Test
    void findByEmail_사용자없음_테스트() {

        // given: 사용자를 저장하지 않는다.

        // when: 존재하지 않는 이메일로 조회한다.
        Optional<User> result =
                userJpaRepository.findByEmail("unknown@test.com");

        // then: 조회 결과가 비어 있는지 확인한다.
        assertThat(result).isEmpty();
    }
}