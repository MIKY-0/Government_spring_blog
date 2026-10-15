package com.tenco.spring_blog.board;

import com.tenco.spring_blog._core.error.Exception403;
import com.tenco.spring_blog._core.error.Exception404;
import com.tenco.spring_blog.reply.ReplyJpaRepository;
import com.tenco.spring_blog.user.SessionUserDto;
import com.tenco.spring_blog.user.User;
import com.tenco.spring_blog.user.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
     * 게시글 목록 조회 페이징 처리(메인 페이지용)
     * @return : 게시글 목록(작성자 정보 포함)
     */
    public BoardResponse.PageDto findAllPage(int page , int size , String keyword) {
        // 1. 1부터 시작하는 번호를 0부터 변환.(0이하가 들어와도 0으로 고정.)
        int pageIndex = Math.max(0 , page - 1);

        // 2. 페이지 크기는 (1 ~ 50)
        int validSize = Math.max(1 , Math.min(size , 50));

        // 최신글이 먼저 오도록 board의 id 내림차순 정렬(주문서 생성) 몇번째 page를 몇개의 게시글 수(size)를 어떤 순서로 가져올지.
        Pageable pageable = PageRequest.of(pageIndex , validSize , Sort.by(Sort.Direction.DESC , "id"));

        Page<Board> boardPage;

        if(keyword == null || keyword.isBlank()) { // isBlank : null이 아닌 상태에서 공백 제거후 개수 확인.
            // 페이징 처리가 된 게시글 목록 조회 요청.(주문 결과 Page)
            boardPage = boardJpaRepository.findAllJoinUser(pageable);
        } else {
            boardPage = boardJpaRepository.findAllKeywordJoinUser(keyword , pageable);
        }

        // 조회 후 트랜잭션 안에서 응답DTO로 변환 처리.
        return new BoardResponse.PageDto(boardPage);
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
        replyJpaRepository.deleteByBoardId(id);
        boardJpaRepository.deleteById(id);

        log.info("게시글 삭제 완료");
    }

}
