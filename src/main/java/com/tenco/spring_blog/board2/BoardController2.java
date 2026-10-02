package com.tenco.spring_blog.board2;

import lombok.RequiredArgsConstructor;
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

    // 목록 전체 조회  http://localhost:8080/ , http://localhost:8080/board/list
    @GetMapping({"/", "/board/list"})
    public String list(Model model) {
        List<Board2> boardList2 = boardPersistRepository2.findAll();
        model.addAttribute("boardList", boardList2);

        return "board/list";
    }

    // 목록 단건 조회 http://localhost:8080/board{id}
    @GetMapping("/board/{id}")
    public String singeList(@PathVariable Long id, Model model) {
        Board2 board2 = boardPersistRepository2.findById(id);

        if (board2 == null) throw new IllegalArgumentException("존재하지 않는 Id");

        model.addAttribute("board", board2);

        return "board/detail";
    }


    // 새 게시글 등록 http://localhost:8080/board/save
    // 1. 등록화면 --> 2. 등록(PRG)
    @GetMapping("/board/save")
    public String saveView() {
        return "board/save-form";
    }

    @PostMapping("/board/save")
    public String saveAction(BoardRequest2.SaveDto req) {
        Board2 board2 = Board2.builder()
                .content(req.getContent())
                .title(req.getTitle())
                .username(req.getUsername())
                .build();

        req.validateSave();

        boardPersistRepository2.createPost(board2);
        return "redirect:/";
    }


    // 게시글 수정 http://localhost:8080/board/{id}/update
    // 1. 수정화면 --> 2. 수정완료(PRG)
    @GetMapping("/board/{id}/update")
    public String updateView(@PathVariable Long id, Model model) {
        Board2 board2 = boardPersistRepository2.findById(id);
        model.addAttribute("board", board2);
        return "board/update-form";
    }

    @PostMapping("/board/{id}/update")
    public String updateAction(@PathVariable Long id, BoardRequest2.UpdateDto req) {

        req.validateUpdate();
        boardPersistRepository2.updatePost(id, req);

        return "redirect:/";
    }


    // 게시글 삭제 http://localhost:8080/board/{id}/delete
    @PostMapping("/board/{id}/delete")
    public String deletePost(@PathVariable Long id) {
//        Board2 board2 = boardPersistRepository2.findById(id);
//
//        if(board2 == null) throw new IllegalReceiveException("존재하지 않는 게시글");

         boardPersistRepository2.deleteOne(id);

        return "redirect:/";
    }
}
