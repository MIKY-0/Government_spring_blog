package com.tenco.spring_blog.board2;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.hibernate.annotations.Temporal;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BoardNativeRepository2 {
    private final EntityManager em ;

    public BoardNativeRepository2(EntityManager em) {
        this.em = em;
    }

    // 전체 목록 조회.
    public List<Board2> findAll() {
        String sql = """
                select * from board_tb
                """;

        // 지금은 Query타입에 담지만 return될때는 Board2타입으로 변환.
        Query query = em.createNativeQuery(sql , Board2.class);

        return query.getResultList();
    }


    // 목록 단건 조회.  존재하지 않는 ID 요청 가능.
    public Board2 findById(Long id) {
        String sql = """
                select * from board_tb
                where id = ?
                """;

        Query query = em.createNativeQuery(sql , Board2.class);
        query.setParameter(1, id);

        try {
            return (Board2) query.getSingleResult();
        }catch (Exception e) {
            return null;
        }
    }


    @Transactional
    // 새 게시글 작성.
    public void createPost(String username , String title , String content) {
        String sql = """
                insert into board_tb(username , title , content , created_at)
                values (? , ? , ? , now())
                """;

        Query query = em.createNativeQuery(sql);
        query.setParameter(1 , username);
        query.setParameter(2 , title);
        query.setParameter(3 , content);

        query.executeUpdate();
    }


    // 수정.
    @Transactional
    public void updatePost(String title , String content , Long id) {
        String sql = """
                update board_tb set title = ? , content = ?
                where id = ?
                """;

        Query query = em.createNativeQuery(sql);
        query.setParameter(1, title);
        query.setParameter(2, content);
        query.setParameter(3, id);
        query.executeUpdate();
    }

    // 삭제.
    @Transactional
    public void deleteOne(Long id) {
        String sql = """
                delete from board_tb
                where id = ?
                """;

        Query query = em.createNativeQuery(sql);
        query.setParameter(1, id);
        query.executeUpdate();
    }
}
