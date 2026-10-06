package com.tenco.spring_blog.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository // IoC + 싱글톤
@RequiredArgsConstructor
public class UserPersistRepository {
    private final EntityManager em;

    // 회원 정보 조회 - 로그인 (사용자 이름 , 비밀번호 확인)
    public User findByUsernameAndPassword(String username , String password) {
        try {
            String jpql = """
                    select u from User u 
                    where u.username = :username and u.password = :password
                    """;

            Query query = em.createQuery(jpql , User.class);
            query.setParameter("username" , username);
            query.setParameter("password" , password);

            return (User) query.getSingleResult();

        }catch (Exception e) {
            // 일치하는 사용자가 없거나 에러 발생시 null 반환. 로그인 실패 의미.
            return null;
        }
    }


    // 회원가입.
    @Transactional
    public User save(User user) {
        // 비영속 상태에 User 객체를 영속성 컨텍스트에 저장.
        em.persist(user);
        // 영속성 컨텍스트가 User 객체를 관리하기 시작.

        // persist() 호출 후 객체는 영속 상태가 되고 트랜잭션 커밋 시점이 INSERT쿼리가 실행됨.
        // 자동 생성된 ID와 생성시간이 user객체에 주입된 상태.
        return user;
    }


    // 사용자명 중복 체크용 조회 메서드.
    public User findByName(String username) {
        // em.find()는 PK만 사용하여 찾을 수 있는데 username은 PK가 아니므로 JPQL 사용.
        String jpql = """
                select u from User u
                where u.username = :username
                """;

        try {
            return em.createQuery(jpql , User.class).
                    setParameter("username" , username).getSingleResult();

        } catch (Exception e) {
            return null;
        }

    }
}
