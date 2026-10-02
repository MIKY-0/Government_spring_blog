//package com.tenco.spring_blog.board;
//
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.Query;
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
///*
//영속성 컨텍스트 활용한 Repository 클래스 만들기
//Repository란, 저장소 , 보관소 , 창고를 의미. 즉, 소프트웨어에서는 데이터를 저장하고 관리하는 곳을 추상화한 개념.
// */
//
//@RequiredArgsConstructor // final 필드 초기화 처리.
//@Repository // IoC + 싱글톤.
//public class BoardPersistRepository {
//    private final EntityManager em;
//
//    // JPQL을 사용한 게시글 목록 전체 조회.
//    public List<Board> findAll() {
//        // JPQL : 엔티티 객체를 대상으로 하는 객체지향 쿼리.
//        // Board는 엔티티 클래스명 , b는 별칭.
//        // 테이블명(board_tb)가 아닌 엔티티명(Board) 사용.
//        String jpql = """
//                select b from Board b order by b.createdAt desc
//                """;
//
//        // createQuery() : JPQL 쿼리 생성.
//        // 두번쨰 매개변수로 반환 타입 지정(타입 안정성 확보)
//        //getResultList() : List<Board>로 반환.
//        return em.createQuery(jpql , Board.class).getResultList();
//
//    }
//
//
//    // PK로 목록 단건 조회(1차 캐시 활용)
//    public Board findById(Long id) {
//        // find() 메서드의 특징 :
//        // 1. 기본키로만 조회 가능.
//        // 2. 1차 캐시 먼저 찾기 시도.
//        // 3. 없으면 DB에서 조회후 1차 캐시에 저장.
//        // 4. 영속상태로 만든 후 반환.
//        Board board = em.find(Board.class , id);
//        return board;
//    }
//
//
//    // JPQL을 활용한 조회 방법.
//    public Board findByIdWithJPQL(Long id) {
//        String jpql = """
//                select b from Board b
//                where b.id = :id
//                """;
//            try {
//                return em.createQuery(jpql, Board.class)
//                        .setParameter("id", id)
//                        .getSingleResult();
//            }catch (Exception e){
//                return null;
//            }
//
//
//        /*
//        JPQL 단점 : 1. 1차캐시 우회하여 항상 DB에 접근.
//        2. 코드가 복잡할 수 있음.
//        3. getSingleResult() 예외 처리 필요. 존재하지 않는 id 요청 대비.
//         */
//    }
//
//
//    // 게시글 저장.
//    @Transactional
//    public Board save(Board board) {
//        // 1. 매개변수로 받은 board는 이시점에서 비영속상태.( -- em.persist(board) 하기 전.)
//        // 아직 영속성 컨텍스트에 관리되지 않은 상태.
//        // DB와 연관없는 순수 자바 객체인 상태.
//
//
//        em.persist(board); // 영속성 컨텍스트에 주입하겠다.
//        // 2. em.persist(board) 이후 : 엔티티를 영속성 컨텍스트에 저장한 시점. JPA가 1차 캐시에 저장함.
//        // board 객체가 영속 상태로 변경됨. 영속성 컨텍스트가 엔티티를 관리하기 시작.
//        // 아직 실제 insert쿼리는 실행되지 않음(쓰기 지연)
//
//
//        // 3. 트랜잭션 커밋 시점에 실제 insert쿼리 실행. --> 이때 영속성 컨텍스트의 변경사항이 실제 DB 에 반영됨.
//        // board 객체의 id 필드에 자동 생성된 값이 할당됨.(auto-increment)
//        return board;
//        // 4. 영속 상태의 객체를 반환.
//        // 이 시점은 트랜잭션이 끝난 시점. -- 자동으로 생성된 id값을 포함한 객체가 반환됨.
//    }
//
//    /*
//     엔티티의 영속 상태 4가지.
//     1. 비영속 상태 : 새로 생성된 객체. 영속성 컨텍스트와 전혀 무관. 객체는 있지만 JPA가 관리하지 않음.
//     2. 영속 상태 : 영속성 컨텍스트에게 관리되는 상태.
//     관리된다 : 이 객체를 계속 추적. 값이 바뀌면 트랜잭션 끝날 때 update로 관리 , 삭제 요청오면 delete로 관리 , 저장요청오면 insert로 관리.
//
//     3. 준영속 상태 : 영속성 컨텍스트에서 분리된 상태.
//4. 삭제 상태 : 삭제 예정 상태.(트랜잭션 commit 또는 flush 시 delete 쿼리 실행.) remove()호출 --> 삭제상태 --> flush/commit --> delete SQL 실행.
//     */
//    private void entityLifecycleEx() {
//        // 1. 비영속 상태.
//        Board board = new Board("제목", "내용", "작성자");
//
//        // 2. 영속 상태.
//        em.persist(board);
//
//        // 3. 준영속 상태. : 영속성 컨텍스트에서 분리된 상태.(영속성이었다가 빠짐.)
//        em.detach(board);
//
//        // 4. 삭제 예정 상태.
//        em.remove(board);
//
//    }
//
//
//    // 게시글 삭제.(영속성 컨텍스트를 활용한 안전한 삭제)
//    @Transactional
//    public void deleteById(Long id){
//        // 1. 먼저 삭제할 엔티티를 영속 상태로 조회부터함.
//        Board boardEntity = em.find(Board.class, id);
//
//        // 2. 엔티티 존재 여부 확인(안전한 삭제)
//        if(boardEntity == null) {
//            throw  new IllegalArgumentException("삭제할 게시글을 찾을 수 없음.");
//        }
//
//        // 3. 영속상태의 엔티티를 삭제 상태로 변경.
//        em.remove(boardEntity);
//        // 삭제 과정
//        // board 엔티티가 영속 --> 삭제로 변경.
//        // 1차 캐시에 해당 엔티티가 제거.
//        // 트랜잭션 커밋 시점에 delete sql 자동 실행.
//
//        // 삭제하는 JPQL 쿼리 작성.
//        // delete from Board b where b.id = :id
//        // 삭제만 하고 리턴이 void이므로 Board.class 필요없음.
//        Query query = em.createQuery("delete from Board b where b.id = :id");
//        query.setParameter("id", id);
//        query.executeUpdate();
//    }
//
//
//    // 게시글 수정
//    @Transactional
//    public void updateById(Long id , BoardRequest.UpdateDto reqDto) {
//        // 1. 수정할 엔티티를 먼저 조회후 영속상태로 만듦.
//        // Board 타입 엔티티를 찾아서 넘겨받은 id와 동일한 데이터를 찾아서 Board 타입으로 반환.
//        Board boardEntity = em.find(Board.class, id);
//
//        // 2. 엔티티 존재 여부 확인.
//        if(boardEntity == null) {
//            throw new IllegalArgumentException("수정할 게시글을 찾을 수 없습니다.");
//        }
//
//        // 엔티티 객체 상태 변경중.
////        boardEntity.setTitle(reqDto.getTitle());
////        boardEntity.setContent(reqDto.getContent());
//
//        boardEntity.update(reqDto);
//
//        // 1차 캐시에 저장된 엔티티 객체의 내부 상태값이 변경되고 트랜잭션이 종료되면 dirty checking 발생.
//        // --> 자동으로 update쿼리 날림.
//
//    }
//}
