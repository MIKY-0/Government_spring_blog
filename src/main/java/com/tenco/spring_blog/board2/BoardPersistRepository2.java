package com.tenco.spring_blog.board2;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BoardPersistRepository2 {
    private final EntityManager em;
    public BoardPersistRepository2(EntityManager em){
        this.em = em;
    }

    // 목록 전체 조회.
    public List<Board2> findAll() {
       String jpql = """
               select b from Board2 b
               """;
        return em.createQuery(jpql , Board2.class).getResultList();
    }


    // 목록 단건 조회
    public Board2 findById(Long id) {
        Board2 board2 = em.find(Board2.class , id);

        return  board2;
    }


    // 새 게시글 등록.
    @Transactional
    public Board2 createPost(Board2 board2) {
        em.persist(board2);

        return board2;
    }


    // 게시글 수정.
    @Transactional
    public void updatePost(Long id, BoardRequest2.UpdateDto req) {
        Board2 board2 = em.find(Board2.class , id);

        if(board2 == null) throw new IllegalArgumentException("존재하지 않는 Id");
        board2.update(req);
    }


    // 게시글 삭제.
    @Transactional
    public void deletePost(Long id) {
        Board2 board2 = em.find(Board2.class, id);

        em.remove(board2);
    }
}
