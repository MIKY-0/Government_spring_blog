//package com.tenco.spring_blog.board2;
//
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.Query;
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import org.jspecify.annotations.Nullable;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
//@Repository     @RequiredArgsConstructor
//public class BoardNativeRepository2 {
//    private final EntityManager em;
//
//    // 목록 전체 조회.
//    public List<Board2> findAll() {
//        String sql = """
//                select * from board_tb
//                """;
//
//        return em.createNativeQuery(sql , Board2.class).getResultList();
//    }
//
//
//    // 목록 단건 조회.
//    public Board2 findById(Long id) {
//        String sql = """
//                select * from board_tb
//                where id = ?
//                """;
//        Query query = em.createNativeQuery(sql , Board2.class);
//        query.setParameter(1 , id);
//
//        try {
//            return (Board2) query.getSingleResult();
//        }catch (Exception e) {
//            return null;
//        }
//    }
//
//
//    // 새 게시글 생성.
//    @Transactional
//    public void createPost(String username , String title , String content) {
//        String sql = """
//                insert into board_tb(username , title , content , created_at)
//                values (? , ? , ? , now())
//                """;
//
//        Query query = em.createNativeQuery(sql);
//        query.setParameter(1 , username);
//        query.setParameter(2 , title);
//        query.setParameter(3 , content);
//
//        query.executeUpdate();
//    }
//
//    // 게시글 수정.
//    @Transactional
//    public void updateById(Long id , String title , String content) {
//        String sql = """
//                update board_tb set title = ? , content = ?
//                """;
//
//        Query query = em.createNativeQuery(sql);
//        query.setParameter(1, title);
//        query.setParameter(2, content);
//
//        query.executeUpdate();
//    }
//
//
//    // 게시글 삭제.
//    @Transactional
//    public void deletedPost(Long id) {
//        String sql = """
//                delete from board_tb
//                where id = ?
//                """;
//
//        Query query = em.createNativeQuery(sql);
//        query.setParameter(1, id);
//        query.executeUpdate();
//    }
//}
