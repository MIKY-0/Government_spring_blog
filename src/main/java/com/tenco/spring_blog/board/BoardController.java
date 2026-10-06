package com.tenco.spring_blog.board;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller     @Slf4j      @RequiredArgsConstructor
public class BoardController {
    private final BoardNativeRepository boardNativeRepository;
    private final BoardPersistRepository boardPersistRepository;

    // GET http://localhost:8080/ , http://localhost:8080/board/list  둘 다 담당.
    @GetMapping({"/" , "/board/list"})
    public String list(Model model) {

        List<Board> boardList = boardPersistRepository.findAll();
        model.addAttribute("boardList" , boardList);

        return "board/list";
    }


    // GET http://localhost:8080/board/3
    @GetMapping("/board/{id}")
    public String detail(@PathVariable(name="id") Long id , Model model) {

        Board boardEntity = boardPersistRepository.findById(id);
//        Board boardEntity = boardPersistRepository.findByIdWithJPQL(id);

        if(boardEntity == null) {
            // 추후 404 에러 페이지를 만들어서 처리할 예정.
            throw new RuntimeException("게시글을 찾을 수 없습니다. : " + id);
        }

        model.addAttribute("board", boardEntity);

        return "board/detail";
    }


    // GET http://localhost:8080/board/save (화면요청.  화면만 뿌림.)
    @GetMapping("/board/save")
    public String saveForm() {

        return "board/save-form";
    }

    // [[코드 추가]]
    // POST http://localhost:8080/board/save (화면요청.  화면만 뿌림.)
    // 스프링부트의 데이터 기본 파싱 전략 key=value.
    // 현재 save-form.mustache에는 name속성이 있다. --> 이 name속성이 있어야 스프링부트가 데이터를 뽑아낼 수 있음.
    // form태그에 있는 값을 자바로 가져오는 것.
    // 이 name속성은 키값이므로 ? 뒤에오는 쿼리. --> RequestParam.


    @PostMapping("/board/save")
    // Spring 폼 데이터를 객체로 변환하는 과정.(데이터 바인딩 메커니즘)
    // 폼 데이터 바인딩 : Spring이 HTTP 요청 파라미터를 객체로 자동 변환.
    public String save(BoardRequest.SaveDto reqDto) {
        // 1. Dto에서 Entity 타입으로 변환.

        // TODO 수정 예정
//        Board board = Board.builder()
//                .title(reqDto.getTitle())
//                .content(reqDto.getContent())
//                .user(reqDto.getUsername())
//                .build();

//        Board boardEntity = boardPersistRepository.save(board); // 이 시점은 영속상태.

        return "redirect:/";

    }

    // =================================================================================================

    // GET http://localhost:8080/board/1/update (수정 화면요청. 지금은 form태그로 던질거라서 restful 맞추지않고 사용.)
    @GetMapping("/board/{id}/update")
    public String updateForm(@PathVariable Long id , Model model) {
        // 샘플 데이터(D)
//        model.addAttribute("board" , sampleBoard(id));

        // 수정하기 화면 요청(먼저 조회부터)
        Board board = boardPersistRepository.findById(id);
        model.addAttribute("board", board);
        return "board/update-form";
    }


    // Post http://localhost:8080/board/1/update (게시글 실제 수정 기능 요청.)
    @PostMapping("/board/{id}/update")
    public String update(@PathVariable Long id ,BoardRequest.UpdateDto reqDto) {

        reqDto.validate(); // 유효성 실패 throw 던짐.
        boardPersistRepository.updateById(id , reqDto);

        // 수정완료했으면 PRG 패턴으로 수정한 게시글페이지로 가서 다시 보여줌.  /board/{id}

        return "redirect:/board/" + id; // 리다이렉트 수정성공한 해당 게시글 화면 이동.
    }

    // ==================================================================================================


    // 게시글 삭제.
    // /board/{{board.id}}/delete
    @PostMapping("/board/{id}/delete")
    public String delete(@PathVariable Long id) {
        boardPersistRepository.deleteById(id);

        // PRG 패턴 사용. -- 삭제완료시 메인페이지로.
        return "redirect:/";
    }
}
