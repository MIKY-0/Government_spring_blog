package com.tenco.spring_blog.purchase;

import com.tenco.spring_blog._core.error.Exception400;
import com.tenco.spring_blog._core.error.Exception404;
import com.tenco.spring_blog.board.Board;
import com.tenco.spring_blog.board.BoardJpaRepository;
import com.tenco.spring_blog.user.User;
import com.tenco.spring_blog.user.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service    @RequiredArgsConstructor        @Transactional(readOnly = true)
@Slf4j
public class PurchaseService {
    private final PurchaseJpaRepository purchaseJpaRepository;
    private final BoardJpaRepository boardJpaRepository;
    private final UserJpaRepository userJpaRepository;


    // 유료 게시글 구매 --> (포인트 차감 + 구매 내역 저장) 기능을 하나의 트랜잭션으로 처리.
    @Transactional
    public void purchase(Long boardId , Long userId){
        // 1. boardId로 게시글 가져옴.
        Board boardEntity = boardJpaRepository.findById(boardId).orElseThrow(() ->
                new Exception404("게시글을 찾을 수 없습니다")); // 구입과 동시에 게시글 삭제가 될 경우 대비.

        // 2. 실제 유료게시글인지 검증. 유료게시글 -> 무료게시글로 변환하는 동시에 구매하는 경우 대비.
        if(!boardEntity.isPremium()) throw new Exception400("유료 게시글이 아닙니다");

        // 3. 작성자는 자신이 작성한 글을 구매하지 못하게. (이미 열람 가능하도록)
        if(boardEntity.isOwner(userId)) throw new Exception400("해당 게시글은 본인이 작성하였으므로 구매 불가합니다.");

        // 4. 유저가 이미 구매했는지 확인.
        if(purchaseJpaRepository.existsByUserIdAndBoardID(userId , boardId)) {
            throw new Exception400("이 게시글은 이미 구매되었습니다");
        }

        // 5. userId로 유저가져와서 이 user가 보유한 포인트 차감연산.(포인트가 부족한지도 확인.)
        User userEntity = userJpaRepository.findById(userId).orElseThrow( () ->
                new Exception404("사용자를 찾을 수 없습니다"));
        userEntity.deductPoint(Board.PREMIUM_PRICE);

        // 6. 구매내역 저장.(DB 반영)
        Purchase purchaseEntity = Purchase.builder()
                .user(userEntity)
                .board(boardEntity)
                .price(Board.PREMIUM_PRICE)
                .build();

        purchaseJpaRepository.save(purchaseEntity);
    }

}
