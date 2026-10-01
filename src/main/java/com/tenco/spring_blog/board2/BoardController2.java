package com.tenco.spring_blog.board2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.h2.engine.Mode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller     @Slf4j      @RequiredArgsConstructor
public class BoardController2 {
    private final BoardNativeRepository2 boardNativeRepository2;

    // 게시글 전체 목록 (GET) http://localhost:8080/ , http://localhost:8080/board/list
    @GetMapping({"/" , "/board/list"})
    public String list(Model model) {
        List<Board2> boardList2 = boardNativeRepository2.findAll();
        model.addAttribute("boardList" , boardList2);

        return "/board/list";
    }


    // 게시글 목록 단건 조회(GET) http://localhost:8080/board/{id} , 존재하지 않는 id요청시 메인페이지로.
    @GetMapping("/board/{id}")
    public String singleList(@PathVariable(name = "id") Long id , Model model) {
        Board2 board2 = boardNativeRepository2.findById(id);

        if(board2 == null) {
            return "redirect:/";
        }else {
            model.addAttribute("board" , board2);
            return  "/board/detail";
        }
    }


    // 게시글 새로 등록.(새 게시글 작성 클릭시 작성화면으로. GET) http://localhost:8080/board/save
    //  username , title , content --> 작성.
    // 1. 작성화면 --> 2.작성 --> 3. 등록 --> 4. 메인화면에 새로 등록한 페이지 다시 띄움.(PRG)
    @GetMapping("/board/save")
    public String newPostView() {
        return "/board/save-form";
    }

    @PostMapping("/board/save")
    public String newPostAction(@RequestParam(name = "username") String username ,
                                @RequestParam(name = "title") String title ,
                                @RequestParam(name = "content") String content) {
        boardNativeRepository2.newPost(username , title , content);

        return "redirect:/";
    }



    // 수정(Post) http://localhost:8080/board/{id}/update
    // 1. 수정화면 --> 2. 수정완료 --> 3.수정한 게시글 메인화면에서 다시 보여주기.(PRG)
    @GetMapping("/board/{id}/update")
    public String updatePostView(@PathVariable Long id , Model model) {
        Board2 board2 = boardNativeRepository2.findById(id);
        model.addAttribute("board" , board2);
        return "/board/update-form";
    }

    @PostMapping("/board/{id}/update")
    public String updatePostAction(@PathVariable Long id ,
                             @RequestParam(name = "title") String title,
                             @RequestParam(name = "content") String content) {
        boardNativeRepository2.updatePost(id , title , content);
        return "redirect:/";
    }


    // 게시글 삭제(Delete)   http://localhost:8080/board/{id}/delete
    // 1. 삭제 요청 --> 메인화면으로 PRG
    @PostMapping("/board/{id}/delete")
    public String deletePost(@PathVariable(name = "id") Long id) {
        boardNativeRepository2.deletePostById(id);

        return "redirect:/";
    }



}
