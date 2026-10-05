package com.tenco.spring_blog.board2;

import lombok.RequiredArgsConstructor;
import org.h2.engine.Mode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BoardController2 {
    private final BoardPersistRepository2 boardPersistRepository2;

    public BoardController2(BoardPersistRepository2 boardPersistRepository2) {
        this.boardPersistRepository2 = boardPersistRepository2;
    }

    // 목록 전체 조회.  http://localhost:8080/  http://localhost:8080/board/list
    @GetMapping({"/", "/board/list"})
    public String list(Model model) {
        model.addAttribute("boardList", boardPersistRepository2.findAll());

        return "board/list";
    }


    // 목록 단건 조회. http://localhost:8080/board/{id}
    @GetMapping("/board/{id}")
    public String detail(@PathVariable(name = "id") Long id, Model model) {
        Board2 board2 = boardPersistRepository2.findById(id);
        if (board2 == null) throw new IllegalArgumentException("존재하지 않는 Id");
        model.addAttribute("board", board2);

        return "board/detail";
    }


    // 새 게시글 작성. http://localhost:8080/board/save
    @GetMapping("/board/save")
    public String newPostView() {
        return "board/save-form";
    }

    @PostMapping("/board/save")
    public String newPostAction(BoardRequest2.SaveDto req) {
        Board2 board2 = Board2.builder()
                .username(req.getUsername())
                .title(req.getTitle())
                .content(req.getContent())
                .build();

        req.validate();
        boardPersistRepository2.createPost(board2);

        return "redirect:/";
    }

    // 게시글 수정. http://localhost:8080/board/{id}/update
    @GetMapping("/board/{id}/update")
    public String updateView(@PathVariable(name = "id") Long id, Model model) {
        model.addAttribute("board", boardPersistRepository2.findById(id));
        return "board/update-form";
    }

    @PostMapping("/board/{id}/update")
    public String updateAction(@PathVariable(name = "id") Long id, BoardRequest2.UpdateDto req) {
        boardPersistRepository2.updatePost(id, req);

        req.validate();

        return "redirect:/board/" + id;
    }


    // 게시글 삭제.  http://localhost:8080/board/{id}/delete
    @PostMapping("/board/{id}/delete")
    public String tryDelete(@PathVariable(name = "id")Long id) {
        boardPersistRepository2.deletePost(id);
        return "redirect:/";
    }
}
