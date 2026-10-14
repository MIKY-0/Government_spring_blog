package com.tenco.spring_blog.reply;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReplyJpaRepository extends JpaRepository<Reply , Long> {
    // 게시글 댓글 목록 조회. (User와 함께 조회 , 오래된 순)
    @Query("select r from Reply r join fetch r.user where r.board.id = :boardId order by r.id asc")
    List<Reply> findAllByBoardIdJoinUser(@Param("boardId") Long boardId);

    // 주의. @Query는 기본적으로 조회(select)로 실행됨. delete , update를 직접 쓸때 반드시 @Modifying 사용.
    // 게시글에 달린 댓글 일괄 삭제.(게시글 삭제 전에 사용 에정)
    @Modifying
    @Query("delete from Reply r where r.board.id = :boardId")
    void deleteByBoardId(@Param("boardId") Long boardId);
}
