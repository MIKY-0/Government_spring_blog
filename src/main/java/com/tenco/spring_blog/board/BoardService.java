package com.tenco.spring_blog.board;

import com.tenco.spring_blog._core.error.Exception400;
import com.tenco.spring_blog._core.error.Exception403;
import com.tenco.spring_blog._core.error.Exception404;
import com.tenco.spring_blog.reply.Reply;
import com.tenco.spring_blog.reply.ReplyJpaRepository;
import com.tenco.spring_blog.reply.ReplyResponse;
import com.tenco.spring_blog.user.SessionUserDto;
import com.tenco.spring_blog.user.User;
import com.tenco.spring_blog.user.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/*
Board 관련 비즈니스 로직을 처리하는 service 계층.
 */
@Service    @Slf4j      @RequiredArgsConstructor
@Transactional(readOnly = true) // 모든 메서드를 읽기 전용 트랜잭션으로 실행. 성능 최적화.(변경 감지 비활성화됨) 데이터 수정 방지.
// 데이터 베이스 Lock을 최소화해 동시성 성능 개선.
public class BoardService {
    private final BoardJpaRepository boardJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final ReplyJpaRepository replyJpaRepository;

    /**
     * 게시글 저장
     * @param saveDto : 게시글 저장 정보.
     * @param sessionUser : 작성자 정보.
     * @return : 저장된 게시글 반환.
     */
    @Transactional
    // save는 데이터 수정이 필요하므로 트랜잭션 읽기 전용 설정을 해제하고 쓰기 가능한 트랜잭션으로 실행.
    public Board save(BoardRequest.SaveDto saveDto , SessionUserDto sessionUser) {
        log.info("게시글 저장 시작 - 제목 : {} , 작성자 : {}" , saveDto.getTitle() , sessionUser.getUsername());

//        Board board = saveDto.toEntity(sessionUser);

        User user = userJpaRepository.getReferenceById(sessionUser.getId());
        Board savedBoard = boardJpaRepository.save(saveDto.toEntity(user));

        log.info("게시글 저장 완료 - ID : {} , 제목 : {}" , savedBoard.getId() , savedBoard.getTitle());
        return savedBoard;
    }

    /**
     *
     * @param id : 게시글 PK
     * @return : 게시글 정보 (작성자 정보 포함)
     */
    public BoardResponse.DetailDto findById(Long id ) {
        log.info("게시글 상세 조회 서비스 - ID : {} " , id);
        Board boardOptional = boardJpaRepository.findByIdJoinUser(id)
                .orElseThrow(() -> new Exception404("해당 게시글이 존재하지 않습니다."));
        log.info("게시글 상세 조회 완료 - 조회된 제목 : {} , 작성자 : {}" ,  boardOptional.getTitle() , boardOptional.getUser().getUsername());
        return new BoardResponse.DetailDto(boardOptional);
    }

    /**
     * 게시글 목록 조회(메인 페이지용)
     * @return : 게시글 목록(작성자 정보 포함)
     */
    public List<BoardResponse.ListDto> findAll() {
        log.info("게시글 목록 조회 시작");
        List<Board> boardList = boardJpaRepository.findAllJoinUser(); // 게시글 정보에는 User(작성자)가 있으므로 여기서도 JOIN FETCH사용.

//        List<BoardResponse.ListDto> dtoList = new ArrayList<>();

        // boardList를 Dto로 변환하는 방법 3가지. 방법 1. (for문으로 전부 순회해서 주입)
//        for(Board board : boardList) {
//            dtoList.add(new BoardResponse.ListDto(board));
//        }

        // 방법 2. 람다 표현식(stream API)
//        dtoList = boardList .stream() .map(b -> new BoardResponse.ListDto(b)) .toList();

        // 방법 3. 메서드 참조 문법. (stream API 축약형)
        return boardList .stream() .map(BoardResponse.ListDto::new) .toList();
    }


    /**
     * 게시글 수정 화면 요청 (인가 검사)
     * @param id
     * @param userId
     * @return
     */
    public BoardResponse.UpdateFormDto findByIdAndUpdateForm(Long id , Long userId) {
        Board board = boardJpaRepository.findByIdJoinUser(id)
                .orElseThrow(() -> new Exception404("해당 게시글은 존재하지 않습니다."));

        // 인가 검사.
        if(!board.isOwner(userId)) throw new Exception403("본인이 작성한 게시글만 수정 가능합니다.");

        return new BoardResponse.UpdateFormDto(board);
    }


    /**
     * 게시글 수정 기능 요청 (권한 체크)
     * @param id : 수정할 게시글 PK
     * @param updateDto : 수정할 정보
     * @param sessionUser : 요청자 정보
     * @return : 수정된 게시글 return.
     */
    @Transactional
    public Board updateById(Long id , BoardRequest.UpdateDto updateDto , SessionUserDto sessionUser) {
        log.info("게시글 수정 시작");
        Board board = boardJpaRepository.findByIdJoinUser(id)
                .orElseThrow(() -> new Exception404("해당 게시글은 존재하지 않습니다.")); // 1. 게시글 조회.
        if(!board.isOwner(sessionUser.getId())) throw new Exception403("본인이 작성한 게시글만 수정할 수 있습니다."); // 2. 권한 체크.
        board.update(updateDto); // 3. 더티체킹을 활용한 수정.
        log.info("게시글 수정 완료");

        return board;
    }


    @Transactional
    public void deleteById(Long id , SessionUserDto sessionUser) {
        log.info("게시글 삭제 시작");
        Board board = boardJpaRepository.findByIdJoinUser(id)
                .orElseThrow(() -> new Exception404("해당 게시글은 존재하지 않습니다."));
        if(!board.isOwner(sessionUser.getId())) throw new Exception403("본인이 작성한 게시글만 삭제할 수 있습니다.");

        // 게시글삭제 , 댓글삭제를 하나의 트랜잭션 안에서.
        boardJpaRepository.deleteById(id);
        replyJpaRepository.deleteByBoardId(id);

        log.info("게시글 삭제 완료");
    }

}
