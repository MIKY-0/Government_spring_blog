package com.tenco.spring_blog.reply;

import com.tenco.spring_blog._core.error.Exception403;
import com.tenco.spring_blog._core.error.Exception404;
import com.tenco.spring_blog.board.Board;
import com.tenco.spring_blog.board.BoardJpaRepository;
import com.tenco.spring_blog.user.SessionUserDto;
import com.tenco.spring_blog.user.User;
import com.tenco.spring_blog.user.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service        @Transactional(readOnly = true)     @RequiredArgsConstructor
public class ReplyService {
    private final ReplyJpaRepository replyJpaRepository;
    private final BoardJpaRepository boardJpaRepository;
    private final UserJpaRepository userJpaRepository;

    // 게시글 댓글 목록 조회. (로그인 안했으면 sessionUserId는 null)
    public List<ReplyResponse.ListDto> findAllByBoardId(Long boardId , Long sessionUserId) {
        List<Reply> replyList = replyJpaRepository.findAllByBoardIdJoinUser(boardId);

        return replyList .stream() .map( r -> new ReplyResponse.ListDto(r , sessionUserId)) .toList();
    }


    // 댓글 작성.
    @Transactional
    public void save(SessionUserDto sessionUserDto , ReplyRequest.SaveDto saveDto){
        User userEntity = userJpaRepository.findById(sessionUserDto.getId()).orElseThrow(() -> new Exception404("해당 유저가 없습니다"));
        Board boardEntity = boardJpaRepository.findById(saveDto.getBoardId()).orElseThrow(() -> new Exception404("해당 게시글이 없습니다"));
        replyJpaRepository.save(saveDto.toEntity(boardEntity , userEntity));
    }


    // 댓글 삭제.
    @Transactional
    public Long deleteById(Long id , SessionUserDto sessionUserDto){
        Reply reply = replyJpaRepository.findById(id).orElseThrow(() -> new Exception404("해당 댓글이 없습니다"));
        if(!reply.isOwner(sessionUserDto.getId())) throw new Exception403("본인이 작성한 댓글만 삭제가능합니다");

        replyJpaRepository.delete(reply);
        return reply.getBoard().getId();
    }
}
