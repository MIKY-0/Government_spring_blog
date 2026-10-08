package com.tenco.spring_blog.board;

import com.tenco.spring_blog._core.error.Exception400;
import com.tenco.spring_blog._core.error.Exception403;
import com.tenco.spring_blog._core.error.Exception404;
import com.tenco.spring_blog.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * 게시글 저장
     * @param saveDto : 게시글 저장 정보.
     * @param sessionUser : 작성자 정보.
     * @return : 저장된 게시글 반환.
     */
    @Transactional
    // save는 데이터 수정이 필요하므로 트랜잭션 읽기 전용 설정을 해제하고 쓰기 가능한 트랜잭션으로 실행.
    public Board save(BoardRequest.SaveDto saveDto , User sessionUser) {
        log.info("게시글 저장 시작 - 제목 : {} , 작성자 : {}" , saveDto.getTitle() , sessionUser.getUsername());

        Board board = saveDto.toEntity(sessionUser);
        Board savedBoard = boardJpaRepository.save(board);

        log.info("게시글 저장 완료 - ID : {} , 제목 : {}" , savedBoard.getId() , savedBoard.getTitle());
        return savedBoard;
    }

    /**
     *
     * @param id : 게시글 PK
     * @return : 게시글 정보 (작성자 정보 포함)
     */
    public Board findById(Long id ) {
        log.info("게시글 상세 조회 서비스 - ID : {} " , id);

        // boardJpaRepository.findById(id) 이 메서드 대신 JOIN FETCH 사용.
        // orElseThrow : Optional객체에 값이 있으면 꺼내고 값이 없으면 예외 발생.

        Board boardOptional = boardJpaRepository.findByIdJoinUser(id).orElseThrow(() -> {
            log.warn("게시글 조회 실패 - ID : {}" , id);
            return new Exception404("존재하지 않는 게시글입니다");
        });

        /*
        boardOptional.getUser().getUsername()에서 User를 한번더 조회. --> N + 1 문제 발생.
         findById(id)로 조회한 게시글 수가 10개라면 --> boardOptional.getUser().getUsername()로 인해
         10개의 각 게시글에 있는 user를 가져와야하므로 10번 조회. --> 총 1 + 10번.
         JOIN FETCH로 board와 user를 join하여 하나의 쿼리로 가져옴.
         */
        log.info("게시글 상세 조회 완료 - 조회된 제목 : {} , 작성자 : {}" ,  boardOptional.getTitle() , boardOptional.getUser().getUsername());
        return boardOptional;
    }

    /**
     * 게시글 목록 조회(메인 페이지용)
     * @return : 게시글 목록(작성자 정보 포함)
     */
    public List<Board> findAll() {
        log.info("게시글 목록 조회 시작");
        List<Board> boardList = boardJpaRepository.findAllJoinUser(); // 게시글 정보에는 User(작성자)가 있으므로 여기서도 JOIN FETCH사용.
        log.info("게시글 목록 조회 완료");
        return  boardList;
    }

    /**
     * 게시글 수정
     * @param id : 수정할 게시글 PK
     * @param updateDto : 수정할 정보
     * @param sessionUser : 요청자 정보
     * @return : 수정된 게시글 return.
     */
    @Transactional
    public Board updateById(Long id , BoardRequest.UpdateDto updateDto , User sessionUser) {
        log.info("게시글 수정 시작");
        Board board = findById(id); // 1. 게시글 조회.
        if(!board.isOwner(sessionUser.getId())) throw new Exception403("본인이 작성한 게시글만 수정할 수 있습니다."); // 2. 권한 체크.
        board.update(updateDto); // 3. 더티체킹을 활용한 수정.
        log.info("게시글 수정 완료");

        return board;
    }


    @Transactional
    public void deleteById(Long id , User sessionUser) {
        log.info("게시글 삭제 시작");
        Board board = findById(id);
        if(!board.isOwner(sessionUser.getId())) throw new Exception403("본인이 작성한 게시글만 삭제할 수 있습니다.");
        boardJpaRepository.deleteById(id);
        log.info("게시글 삭제 완료");
    }

}
