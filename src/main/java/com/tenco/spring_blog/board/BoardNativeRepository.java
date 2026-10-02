package com.tenco.spring_blog.board;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository // IoC , @Repository : 스프링이 데이터 접근 계층으로 인식. --> "이 클래스는 DB와 데이터를 주고받는 역할이야."
// 데이터베이스 예외를 스프링 예외로 변환해줌.

@RequiredArgsConstructor // final 필드에 대한 생성자를 .class 생성시 자동으로 생성.  DI 처리. 아래 생성자 주석 역할.
public class BoardNativeRepository {
    // EntityManager JPA의 핵심 인터페이스.
    // 데이터베이스와의 모든 작업 담당.(Statement , PreparedStatement같은 역할)
    private final EntityManager em;

    // DI.
//    public BoardNativeRepository(EntityManager em) {
//        this.em = em;
//    }

    @Transactional // 자바에서 작성해본 setAutocommit() , rollback() , commit()과 같은 트랜잭션 역할.
    public void save(String title , String content , String username) {
        // Statement , PreparedStatement , ResultSet은 Query가 담당.
        Query query = em.createNativeQuery("insert into board_tb(title , content , username , created_at)" +
                "values(? , ? , ? , now())");

        query.setParameter(1, title);
        query.setParameter(2, content);
        query.setParameter(3, username);

        query.executeUpdate();
    }

    public List<Board> findAll() {
        String sql = """
                select * from board_tb order by id desc
                """;

        // Board.class가 없으면 Object클래스로 들어옴. Board객체로 받아야하므로 작성.
        Query query = em.createNativeQuery(sql , Board.class); // 담아줄 데이터("?")가 없으니 createNativeQuery 사용.

        // getResultList() : while(rs.next()) 알아서 동작함. 반환할 행이 다수이므로 getResultList().
        return  query.getResultList();

    }

    public Board findById(Long id) {
        String sql = """
                select * from board_tb 
                where id = ? 
                """;

        Query query = em.createNativeQuery(sql , Board.class);
        query.setParameter(1 , id);

        // 반환할 행이 1건이므로 getSingleResult(). --> 반환타입이 Object이므로 Board로 형변환.
        // 형변환. --> ?인 id값에 존재하는 id가아닌 1234같은 존재하지 않는 id를 넣으면 null이 반환됨.
        // 그런데 밑에서 형변환하고있어서 null을 형변환하려니까 오류가 생김. --> try-catch.
        try {
            return (Board) query.getSingleResult();
        }catch(Exception e) {
            return null;
        }

    }

    @Transactional // select가 아니면 웬만하면 모두 트랜잭션 걸어주자.
    public void deleteById(Long id) {
        String sql = """
                delete from board_tb
                where id = ?
                """;

        Query query = em.createNativeQuery(sql);
        query.setParameter(1, id);
        query.executeUpdate();

    }

    @Transactional
    public boolean updateById(String title, String content, Long id) {
        String sql = """
                update board_tb set title = ? , content = ?
                where id = ?
                """;

        Query query = em.createNativeQuery(sql);
        query.setParameter(1, title);
        query.setParameter(2 , content);
        query.setParameter(3, id);

        if(query.executeUpdate() >= 1) return true;
        else return false;
    }
}
