package com.tenco.spring_blog.board2;

import lombok.RequiredArgsConstructor;
import org.h2.engine.Mode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BoardController2 {
    private final BoardPersistRepository2 boardPersistRepository2;

    // 전체 목록 조회. http://localhost:8080/   http://localhost:8080/boatd/list
    @GetMapping({"/", "/board/list"})
    public String list(Model model) {
        List<Board2> boardList2 = boardPersistRepository2.findAll();
        model.addAttribute("boardList", boardList2);

        return "board/list";
    }


    // 목록 단건 조회. http://localhost:8080/board/{id}
    @GetMapping("/board/{id}")
    public String detail(@PathVariable(name = "id") Long id, Model model) {
        Board2 boardEntity2 = boardPersistRepository2.findById(id);

        if (boardEntity2 == null) throw new IllegalArgumentException("게시글 없음");
        model.addAttribute("board", boardEntity2);

        return "board/detail";
    }


    // 새 게시글 작성. http://localhost:8080/board/save
    // 화면 --> 작성완료(PRG)
    @GetMapping("/board/save")
    public String saveView() {
        return "board/save-form";
    }

    @PostMapping("/board/save")
    // 브라우저에서 title , username , content값 요청을 보내면 이들은 폼태그 내부에있으며 input태그의 name값도 모두 각각 갖고있다.
    // Spring이 이와 BoardRequest2.SaveDto를 보고 서로 일치하는 지 확인 -> Dto에 들어온 값을 넣어서 세팅해준다.(setTitle...)
    // 만약 Dto와 맞는 값이 없으면 바로 에러를 터뜨리지 않고 그 필드는 null로 보냄.
    public String saveAction(BoardRequest2.SaveDto reqDto) {
        Board2 board2 = Board2.builder()
                .username(reqDto.getUsername())
                .title(reqDto.getTitle())
                .content(reqDto.getContent())
                .build();

        Board2 boardEntity2 = boardPersistRepository2.save(board2);

        return "redirect:/";
    }


    // 게시글 수정.  http://localhost:8080/board/{id}/update
    // 수정화면 --> 수정완료.(PRG)
    @GetMapping("/board/{id}/update")
    public String updateView(@PathVariable(name = "id") Long id , Model model) {
        Board2 board2 = boardPersistRepository2.findById(id);
        model.addAttribute("board", board2);
        return "board/update-form";
    }

    @PostMapping("/board/{id}/update")
    public String updateAction(@PathVariable(name = "id") Long id , BoardRequest2.UpdateDto reqDto) {
        boardPersistRepository2.updatePost(id, reqDto);
        return "redirect:/";
    }


    // 게시글 삭제. http://localhost:8080/board/{id}/delete
}
