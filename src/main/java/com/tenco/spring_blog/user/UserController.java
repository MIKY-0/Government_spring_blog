package com.tenco.spring_blog.user;

import com.tenco.spring_blog._core.error.Exception400;
import com.tenco.spring_blog._core.error.Exception404;
import com.tenco.spring_blog._core.util.Define;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller // IoC (제어의 역전) 싱글톤 패턴으로 관리됨.
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    // 회원가입.  GET   http://localhost:8080/join
    @GetMapping("/join")
    public String joinForm(){
        return "user/join-form";
    }

    @PostMapping("/join")
    public String joinAction(UserRequest.JoinDto joinDto) {
            // 1. 유효성 검사.
            joinDto.validate();
            userService.join(joinDto);

            return "redirect:/login";
    }


    // 로그인 처리. GET   http://localhost:8080/login
    @GetMapping("/login")
    public String loginForm(){
        return "user/login-form";
    }

    // 예외적으로 post요청.
    @PostMapping("/login")
    public String loginAction(UserRequest.LoginDto req , HttpSession session) {
            // 1. 입력 데이터 검증.
            req.validate();
            User user = userService.login(req);

            user.setPassword(null); // 패스워드는 로그인할 때만 필요하고 그 이후로는 계속 들고있을 필요 없음.
            // 패스워드까지 세션에 저장시켜놓으면 털릴수도있으므로 패스워드 사용했으면 null로 설정.
            session.setAttribute(Define.SESSION_USER , user); // header.mustache의 키값.

            return "redirect:/";
    }


    // GET http://localhost:8080/user/update
    @GetMapping("/user/update")
    public String updateForm(Model model , HttpSession session){
        // 1. 인증 검사.
        User sessionUser = (User) session.getAttribute(Define.SESSION_USER);

        User userEntity = userService.findById(sessionUser.getId());
        model.addAttribute("user" , userEntity);

        return "user/update-form";
    }

    @PostMapping("/user/update")
    public String update(HttpSession session , UserRequest.UpdateDto updateDto){
            updateDto.validate();

        User sessionUser = (User) session.getAttribute(Define.SESSION_USER);

            // 4. 세션 동기화 : 수정된 정보를 세션에 반영.
            User updatedUser = userService.updateById(sessionUser.getId() , updateDto);
            updatedUser.setPassword(null);
            session.setAttribute(Define.SESSION_USER , updatedUser);

            return "redirect:/";
    }


    // GET http://localhost:8080/logout
    @GetMapping("/logout")
    public String logoutForm(HttpSession session){
        // 세션 무효화 처리.
        session.invalidate();
        return "redirect:/";
    }
}
