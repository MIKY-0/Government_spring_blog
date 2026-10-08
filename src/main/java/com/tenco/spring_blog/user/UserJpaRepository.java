package com.tenco.spring_blog.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface UserJpaRepository extends JpaRepository<User , Long> {
    // 기본적인 CRUD기능 다 만들어져있음.
    /*
    1. 등록 및 수정 : save(Board entity) - 엔티티를 DB에 저장함. ID가 없으면 자동으로 insert , 있으면 update 실행.
    2. 단건 조회 : findById(Long id) - JpaRepository 설정한 제네릭 타입이 들어감. - ID로 엔티티를 조회하면 Optional<Board> 타입 반환.
    3. 전체 조회 : findAll() - 테이블의 모든 데이터를 조회하여 List<Board>로 반환.
    4. 삭제 : deleteById(Long id) - 해당 ID를 가진 엔티티 삭제.
    5. 데이터 개수 : count() - 전체 레코드의 개수를 반환.
    6. 존재 여부 확인 : existById(Long id) - 해당 ID를 가진 데이터가 있는지 확인하여 boolean 반환.
     */

    // 사용자명과 비밀번호로 조회(로그인용)
    @Query("select u from User u where u.username = :username and u.password = :password")
    Optional<User> findByUsernameAndPassword(@Param("username") String username , @Param("password") String password);

    //  사용자명 중복체크.
    @Query("select u from User u where u.username = :username")
    Optional<User> findByUsername(@Param("username") String username);

    // 사용자 정보 업데이트는 JPA의 더티 체킹 활용.

}
