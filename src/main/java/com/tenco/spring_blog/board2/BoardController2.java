package com.tenco.spring_blog.board2;

import org.h2.engine.Mode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class BoardController2 {
    private final BoardNativeRepository2 boardNativeRepository2;

    public BoardController2(BoardNativeRepository2 boardNativeRepository2) {
        this.boardNativeRepository2 = boardNativeRepository2;
    }


    // 전체 목록 조회.  http://localhost:8080/  http://localhost:8080/board/list
    @GetMapping({"/" , "/board/list"})
    public String list(Model model) {

        List<Board2> boardList2 = boardNativeRepository2.findAll();
        model.addAttribute("boardList" , boardList2);

        return "board/list";
    }



    // 목록 단건 조회. http://localhost:8080/board/{id}  존재하지 않는 ID 요청시 메인페이지로.
    @GetMapping("/board/{id}")
    public String singleList(@PathVariable(name = "id") Long id , Model model){
        Board2 board2 = boardNativeRepository2.findById(id);
        model.addAttribute("board" , board2);

        if(board2 == null) {
            return "redirect:/";
        }else {
            return "board/detail";
        }
    }


    // 새 게시글 작성. http://localhost:8080/board/new
    // 1. 작성화면 --> 2. 작성후 메인화면.
    @GetMapping("/board/new")
    public String newPostView() {
        return "board/create-form";
    }

    @PostMapping("/board/new")
    public String newPostAction(@RequestParam(name = "name") String username ,
                                @RequestParam(name = "title") String title ,
                                @RequestParam(name = "content") String content){

         boardNativeRepository2.createPost(username , title , content);

        return "redirect:/";
    }


    // 게시글 수정. http://localhost:8080/board/{id}/update
    // 1. 수정화면 --> 2. 수정 --> 3. 수정완료시 수정한 게시글 다시 화면에 띄움(PRG)
    @GetMapping("/board/{id}/update")
    public String updatePostView(@PathVariable(name = "id") Long id ,
                                 Model model) {
        Board2 board2 = boardNativeRepository2.findById(id);
        model.addAttribute("board" , board2);

        return "board/update-form";
    }

    @PostMapping("/board/{id}/update")
    public String updatePostAction(@PathVariable(name = "id") Long id ,
                                   @RequestParam(name = "title")String title ,
                                   @RequestParam(name = "content") String content){
        boardNativeRepository2.updatePost(title , content , id);
        return "redirect:/board/" + id;
    }

    // 게시글 삭제. http://localhost:8080/board/{id}/delete
    // 1. 삭제 --> 2. 메인화면으로 (PRG)
    @PostMapping("/board/{id}/delete")
    public String deletePost(@PathVariable(name = "id") Long id){
        boardNativeRepository2.deleteOne(id);

        return "redirect:/";
    }

}
