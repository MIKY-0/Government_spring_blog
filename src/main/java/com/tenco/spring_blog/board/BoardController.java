package com.tenco.spring_blog.board;

import com.tenco.spring_blog._core.error.Exception403;
import com.tenco.spring_blog._core.util.Define;
import com.tenco.spring_blog.user.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller     @Slf4j      @RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;
//    private final HttpSession session;  필드로 올려도 되지만 실행이 무거워짐. 웬만하면 메서드에 의존관계로 넣자.

    // GET http://localhost:8080/ , http://localhost:8080/board/list  둘 다 담당.
    // excludePathPatterns로 제외되어 로그인 접근 가능.
    @GetMapping({"/" , "/board/list"})
    public String list(Model model) {
        List<Board> boardList = boardService.findAll();
        model.addAttribute("boardList" , boardList);

        return "board/list";
    }


    // GET http://localhost:8080/board/3
    @GetMapping("/board/{id}")
    public String detail(@PathVariable(name="id") Long id , Model model) {
        Board board = boardService.findById(id);

        model.addAttribute("board", board);

        return "board/detail";
    }


    // GET http://localhost:8080/board/save (화면요청.  화면만 뿌림.)
    @GetMapping("/board/save")
    public String saveForm(HttpSession session) {
        // 인터셉터에서 인증검사 진행됨.
        // 1. 인증 검사 : 로그인 안된 사용자는 이 페이지에 접근 못하게 처리.
        // getAttribute는 Object타입이므로 User로 변환.
//        User sessionUser = (User)session.getAttribute(Define.SESSION_USER);

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
    public String save(BoardRequest.SaveDto saveDto , HttpSession session) {
        // 1. 유효성 검사. (로그인 사용자가 올바른 값을 입력했는지)
            saveDto.validate(); // 입력 데이터 검증.

        // 2. 인증 검사.(로그인 사용자가 맞는지)
        User sessionUser = (User) session.getAttribute(Define.SESSION_USER);

            Board savedBoard = boardService.save(saveDto , sessionUser); // Board 저장.

            return "redirect:/";
    }


    // TODO 인가처리를 서비스로 이동 예정.
    // GET http://localhost:8080/board/1/update (수정 화면요청. 지금은 form태그로 던질거라서 restful 맞추지않고 사용.)
    @GetMapping("/board/{id}/update")
    public String updateForm(@PathVariable Long id , Model model , HttpSession session , RedirectAttributes ra) {
        User sessionUser = (User) session.getAttribute(Define.SESSION_USER);

        // 2. 권한 체크를 위한 게시글 조회.
        Board boardEntity = boardService.findById(id);
        if (!boardEntity.isOwner(sessionUser.getId())) throw new Exception403("수정할 권한이 없습니다.");

            model.addAttribute("board", boardEntity);

            return "board/update-form";
    }


    // Post http://localhost:8080/board/1/update (게시글 실제 수정 기능 요청.)
    @PostMapping("/board/{id}/update")
    public String update(@PathVariable Long id ,BoardRequest.UpdateDto updateDto , HttpSession session) {
        updateDto.validate();
        User sessionUser = (User) session.getAttribute(Define.SESSION_USER);
        boardService.updateById(id , updateDto , sessionUser);

        return "redirect:/board/" + id;
    }


    // 게시글 삭제.
    // /board/{{board.id}}/delete
    @PostMapping("/board/{id}/delete")
    public String delete(@PathVariable Long id , HttpSession session) throws Exception403{
            User sessionUser = (User) session.getAttribute(Define.SESSION_USER);
            boardService.deleteById(id , sessionUser);

           return "redirect:/";
    }
}
