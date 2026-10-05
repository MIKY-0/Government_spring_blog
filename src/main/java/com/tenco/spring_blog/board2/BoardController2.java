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
@RequiredArgsConstructor
public class BoardController2 {
    private final BoardNativeRepository2 boardNativeRepository2;

    // 목록 전체 조회. http://localhost:8080/   http://localhost:8080/board/list
    @GetMapping({"/", "/board/list"})
    public String list(Model model) {
        model.addAttribute("boardList", boardNativeRepository2.findAll());

        return "board/list";
    }

    // 목록 단건 조회.  http://localhost:8080/board/{id}
    @GetMapping("/board/{id}")
    public String detail(@PathVariable(name = "id") Long id, Model model) {
        Board2 board2 = boardNativeRepository2.findById(id);

        if (board2 == null) throw new IllegalArgumentException("존재하지 않는 Id");

        model.addAttribute("board", board2);

        return "board/detail";
    }


    // 새 게시글 작성.  http://localhost:8080/board/new
    @GetMapping("/board/save")
    public String newPostView() {
        return "board/save-form";
    }

    @PostMapping("/board/save")
    public String newPostAction(@RequestParam("username") String username,
                                @RequestParam("title") String title,
                                @RequestParam("content") String content) {

        boardNativeRepository2.createPost(username, title, content);
        return "redirect:/";
    }


    // 게시글 수정.  http://localhost:8080/board/{id}/update
    @GetMapping("/board/{id}/update")
    public String updateView(@PathVariable(name = "id") Long id, Model model) {
        Board2 board2 = boardNativeRepository2.findById(id);
        model.addAttribute("board", board2);
        return "board/update-form";
    }

    @PostMapping("/board/{id}/update")
    public String updateAction(@PathVariable(name = "id") Long id,
                               @RequestParam("title") String title,
                               @RequestParam("content") String content) {
        boardNativeRepository2.updateById(id, title, content);
        return "redirect:/board/" + id;
    }


    // 게시글 삭제.  http://localhost:8080/board/{id}/delete
    @PostMapping("/board/{id}/delete")
    public String deletePost(@PathVariable(name = "id") Long id) {

        boardNativeRepository2.deletedPost(id);
        return "redirect:/";
    }
}
