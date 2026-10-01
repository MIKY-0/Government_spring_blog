package com.tenco.spring_blog.controller;

import org.h2.engine.Mode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
public class BoardController {
    // GET http://localhost:8080/ , http://localhost:8080/board/list  둘 다 담당.
    @GetMapping({"/" , "/board/list"})
    public String list(Model model) {

        // 임시 데이터
        model.addAttribute("boardList" , List.of(
                Map.of("id" , 1 , "title" , "첫번째 글") ,
                Map.of("id" , 2 , "title" , "두번째 글") ,
                Map.of("id" , 3 , "title" , "세번째 글")
        ));

        return "board/list";
    }

    // GET http://localhost:8080/board/3
    @GetMapping({"/board/{id}"})
    public String detail(@PathVariable(name="id") Long id , // name을 설정하면 name에서는 {id}와 맞추고 Long은 내가 원하는 변수명 사용 가능.
                         Model model) {
        model.addAttribute(("board"), sampleBoard(id));
        return "board/detail";
    }

    // GET http://localhost:8080/board/save (화면요청.  화면만 뿌림.)
    @GetMapping({"/board/save"})
    public String saveForm() {

        return "board/save-form";
    }

    // GET http://localhost:8080/board/1/update (수정 화면요청. 지금은 form태그로 던질거라서 restful 맞추지않고 사용.)
    @GetMapping({"/board/{id}/update"})
    public String updateForm(@PathVariable Long id , Model model) {
        model.addAttribute("board" , sampleBoard(id));
        return "board/update-form";
    }

    // TODO
    // 뼈대용 임시 게시글 한개(데이터베이스 연결시 삭제 예정)
    private Map<String , Object> sampleBoard(Long id) {
        return Map.of("id", id , "title" , id + "번째 글" ,  "content" , "임시내용" ,
                "username" , "김민수");
    }
}
