package com.tenco.spring_blog.board2;

import com.tenco.spring_blog.board.Board;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository     @RequiredArgsConstructor // <-- final 때문에 초기화시켜줌.
public class BoardNativeRepository2 {
     private final EntityManager em;

     // 목록 전체 조회 기능.
    public List<Board2> findAll() {
        String sql = """
                select * from board_tb
                """;

        // createNative가 Object반환하므로 Board2.class로 바꿈.
        Query query = em.createNativeQuery(sql , Board2.class);

        // 다수 목록조회이므로.
        return query.getResultList();
    }

    // 목록 단건 조회 기능.
    public Board2 findById(Long id) {
        String sql = """
                select * from board_tb
                where id = ?               
                """;

        Query query = em.createNativeQuery(sql);
        query.setParameter(1, id);
        try {
            return (Board2)query.getSingleResult();
        }catch (Exception e) {
            return null;
        }
    }


    // 새로운 게시글 추가 기능.
    @Transactional
    public void newPost(String username , String title , String content) {
        String sql = """
                insert into board_tb (username , title , content) 
                values(? , ? , ?)                   
                """;

        Query query = em.createNativeQuery(sql);
        query.setParameter(1, username);
        query.setParameter(2, title);
        query.setParameter(3, content);

        query.executeUpdate();

    }

    @Transactional
    public void updatePost(Long id , String title , String content) {
        String sql = """
                update board_tb set title = ? ,content = ?
                where id = ? 
                """;

        Query query = em.createNativeQuery(sql);

        query.setParameter(1, id);
        query.setParameter(2, title);
        query.setParameter(3, content);

        query.executeUpdate();
    }
}
