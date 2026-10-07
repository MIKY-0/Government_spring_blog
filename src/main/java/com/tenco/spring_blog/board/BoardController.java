package com.tenco.spring_blog.board;

import com.tenco.spring_blog._core.error.Exception403;
import com.tenco.spring_blog._core.error.Exception404;
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
    private final BoardPersistRepository boardPersistRepository;
//    private final HttpSession session;  필드로 올려도 되지만 실행이 무거워짐. 웬만하면 메서드에 의존관계로 넣자.

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
            throw new Exception404("게시글을 찾을 수 없습니다. : " + id);
        }

        model.addAttribute("board", boardEntity);

        return "board/detail";
    }


    // GET http://localhost:8080/board/save (화면요청.  화면만 뿌림.)
    @GetMapping("/board/save")
    public String saveForm(HttpSession session) {
        // 1. 인증 검사 : 로그인 안된 사용자는 이 페이지에 접근 못하게 처리.
        // getAttribute는 Object타입이므로 User로 변환.
        User sessionUser = (User)session.getAttribute(Define.SESSION_USER);

        // sessionUser가 null --> 로그인 안된 사용자가 /board/save URL 요청시 로그인 화면으로 보냄.
        if(sessionUser == null) return "redirect:/login";

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
        // 1. 인증 검사.(로그인 사용자가 맞는지)
        User sessionUser = (User) session.getAttribute(Define.SESSION_USER);
        if(sessionUser == null) return "redirect:/login";

        // 2. 유효성 검사. (로그인 사용자가 올바른 값을 입력했는지)

            saveDto.validate(); // 입력 데이터 검증.
            Board board = saveDto.toEntity(sessionUser); // DTO에서 Entity 객체생성.
            Board savedBoard = boardPersistRepository.save(board); // Board 저장.

            return "redirect:/";

    }

    // =================================================================================================

    // GET http://localhost:8080/board/1/update (수정 화면요청. 지금은 form태그로 던질거라서 restful 맞추지않고 사용.)
    @GetMapping("/board/{id}/update")
    public String updateForm(@PathVariable Long id , Model model , HttpSession session , RedirectAttributes ra) {
        User sessionUser = (User) session.getAttribute(Define.SESSION_USER);
        if(sessionUser == null) return "redirect:/login";

        // 2. 권한 체크를 위한 게시글 조회.
        Board boardEntity = boardPersistRepository.findById(id);

        // 3. 권한 체크 : 본인이 작성한 게시글만 수정 가능.
        if(!boardEntity.isOwner(sessionUser.getId())) throw new Exception403("이 게시글에는 수정 권한이 없습니다.");

            model.addAttribute("board", boardEntity);

            return "board/update-form";

    }


    // Post http://localhost:8080/board/1/update (게시글 실제 수정 기능 요청.)
    @PostMapping("/board/{id}/update")
    public String update(@PathVariable Long id ,BoardRequest.UpdateDto updateDto , HttpSession session) {
        // 1. 인증검사.
        User sessionUser = (User) session.getAttribute(Define.SESSION_USER);
        if(sessionUser == null) return "redirect:/login";

        // 2. 권한 검사.
            Board boardEntity = boardPersistRepository.findById(id);

            // 유효성 실패 throw 던짐.
            if(!boardEntity.isOwner(sessionUser.getId())) throw new Exception403("이 게시글에는 수정 권한이 없습니다.");

            // 3. 입력데이터 검증.
            updateDto.validate();

            // 4. 더티체킹을 통한 수정 실행.
            boardPersistRepository.updateById(id , updateDto);

            // 5. 수정완료 후 해당 게시글 상세보기로 이동.
            return "redirect:/board/" + id;

    }

    // ==================================================================================================


    // 게시글 삭제.
    // /board/{{board.id}}/delete
    @PostMapping("/board/{id}/delete")
    public String delete(@PathVariable Long id , HttpSession session) throws Exception403{
        // 1. 인증검사(로그인 여부 확인)
        User sessionUser = (User) session.getAttribute(Define.SESSION_USER);
        if(sessionUser == null) return "redirect:/login";

        // 2. 권한 확인 -- 로그인 했지만 내가 작성한 글인지 여부 확인.
        // 2-1. 관리자 광고성 게시글을 다른사람이 삭제도 가능.
           // 2. 현재 로그인사용자가 삭제가능한 게시글 조회. (권한 체크를 위해)
           Board boardEntity = boardPersistRepository.findById(id);

           // 3. 권한 체크. 내가 작성한 게시글만 삭제가능하도록.
           if(!boardEntity.isOwner(sessionUser.getId())) throw new Exception403("이 게시글에는 삭제 권한이 없습니다.");

           // 4. 권한 확인 후 삭제 실행.
           boardPersistRepository.deleteById(id);

           // 5. 삭제 성공 후 메인 페이지로 PRG.
           return "redirect:/";

    }
}
