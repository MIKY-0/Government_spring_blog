package com.tenco.spring_blog.board2;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository     @RequiredArgsConstructor
public class BoardPersistRepository2 {
    private final EntityManager em;


    // 목록 전체 조회.
    public List<Board2> findAll() {
        String jpql = """
                select b from Board2 b order by b.createdAt desc
                """;
        // 위 sql결과를 Board2객체로 변환하고 타입도 Board2로 만든다. 그런 후 위 결과들을 영속상태로 등록됨.
        return em.createQuery(jpql , Board2.class).getResultList();
    }


    // 목록 단건 조회.
    public Board2 findById(Long id) {
        // 받아온 id(pk)를 가지고 Board2엔티티에서 찾는다. --> 일치하는 id가 있는 결과행을 board2객체로 만들고 board2타입이 된다.
        // --> 이 결과를 영속상태로 등록.
        Board2 board2 = em.find(Board2.class, id);
        return board2;
    }


    // 게시글 새로 등록.
    @Transactional
    public Board2 save(Board2 board2) {
        em.persist(board2);
        return board2;
    }


    // 게시글 수정.
    public void updatePost(Long id, BoardRequest2.UpdateDto reqDto) {
        Board2 board2 = em.find(Board2.class, id);
        board2.update(reqDto);

    }
}
