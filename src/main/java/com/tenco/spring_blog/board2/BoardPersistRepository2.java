//package com.tenco.spring_blog.board2;
//
//import com.sun.nio.sctp.IllegalReceiveException;
//import jakarta.persistence.EntityManager;
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
//@Repository     @RequiredArgsConstructor
//public class BoardPersistRepository2 {
//    private final EntityManager em;
//
//    // 목록 전체 조회.
//    public List<Board2> findAll() {
//        String jpql = """
//                select b from Board2 b
//                """;
//
//        List<Board2> boardList2 = em.createQuery(jpql , Board2.class).getResultList();
//        return boardList2;
//    }
//
//
//    // 목록 단건 조회.
//    public Board2 findById(Long id) {
//        try {
//            Board2 board2 = em.find(Board2.class, id);
//            return board2;
//        }catch (Exception e){
//            return null;
//        }
//
//
//    }
//
//
//
//    // 게시글 등록.
//    @Transactional
//    public Board2 createPost(Board2 board2) {
//        em.persist(board2);
//        return board2;
//    }
//
//
//    // 게시글 수정.
//    @Transactional
//    public void updatePost(Long id , BoardRequest2.UpdateDto req) {
//        Board2 board2 = em.find(Board2.class , id);
//
//        if(board2 == null) throw new IllegalArgumentException("존재하지 않는 ID");
//
//        board2.update(req);
//    }
//
//    // 삭제.
//    @Transactional
//    public void deleteOne(Long id) {
//        Board2 board2 = em.find(Board2.class , id);
//
//        if(board2 == null) throw new IllegalReceiveException("존재하지 않는 게시글");
//
//        em.remove(board2);
//    }
//}
