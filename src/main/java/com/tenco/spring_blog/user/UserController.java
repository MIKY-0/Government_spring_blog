package com.tenco.spring_blog.user;

import com.tenco.spring_blog._core.error.Exception400;
import com.tenco.spring_blog._core.error.Exception404;
import com.tenco.spring_blog._core.util.Define;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller // IoC (제어의 역전) 싱글톤 패턴으로 관리됨.
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    // application.yaml 의 변수를 가져올때 사용.
    @Value("${oauth.kakao.client-id}")
    private String kakaoClientId;

    @Value("${oauth.kakao.client-secret}")
    private String kakaoClientSecret;

    @Value("${oauth.kakao.redirect-uri}")
    private String redirectUri;

    // 서버가 시작될 때 한번 실행되어 콘솔에 값이 찍힘.(테스트용)
    @PostConstruct
    public void init(){
        log.info("카카오 REST API Key 확인 : {}" , kakaoClientId);
        log.info("카카오 Client Secret 확인 : {}" , kakaoClientSecret);
    }

    @GetMapping("/kakao-login")
    public String kakaoLogin(){
        String url = "https://kauth.kakao.com/oauth/authorize" +
                "?client_id=" + kakaoClientId +
                "&redirect_uri=" + redirectUri +
                "&response_type=code";

        return "redirect:" + url;
    }

    // 프로필 삭제
    @PostMapping("/user/profile-image/delete")
    public String deleteProfileImage(HttpSession session){
        SessionUserDto sessionUserDto = (SessionUserDto) session.getAttribute(Define.SESSION_USER);
        userService.deleteProfileImage(sessionUserDto.getId());

        return "redirect:/user/detail";
    }


    // GET  http://localhost:8080/user/detail
    @GetMapping("/user/detail")
    public String detail(HttpSession session , Model model){
        SessionUserDto sessionUserDto = (SessionUserDto) session.getAttribute(Define.SESSION_USER);
        UserResponse.DetailDto user = userService.findDetail(sessionUserDto.getId());
        model.addAttribute("user", user);

        return "user/detail";
    }



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
            SessionUserDto sessionUserDto = userService.login(req);

            // 패스워드까지 세션에 저장시켜놓으면 털릴수도있으므로 패스워드 사용했으면 null로 설정.
            session.setAttribute(Define.SESSION_USER , sessionUserDto); // header.mustache의 키값.

            return "redirect:/";
    }


    // GET http://localhost:8080/user/update
    @GetMapping("/user/update")
    public String updateForm(Model model , HttpSession session){
        // 1. 인증 검사.
        SessionUserDto sessionUserDto = (SessionUserDto) session.getAttribute(Define.SESSION_USER);
        UserResponse.updateFormDto userUpdateFormDto = userService.findUpdateForm(sessionUserDto.getId());
        model.addAttribute("user" , userUpdateFormDto);

        return "user/update-form";
    }

    @PostMapping("/user/update")
    public String update(HttpSession session , UserRequest.UpdateDto updateDto , Model model){

        SessionUserDto sessionUserDto = (SessionUserDto) session.getAttribute(Define.SESSION_USER);

            // 4. 세션 동기화 : 수정된 정보를 세션에 반영.
            SessionUserDto updateSessionUserDto = userService.updateById(sessionUserDto.getId() , updateDto);
            session.setAttribute(Define.SESSION_USER , updateSessionUserDto);

            return "redirect:/user/detail";
    }


    // GET http://localhost:8080/logout
    @GetMapping("/logout")
    public String logoutForm(HttpSession session){
        // 세션 무효화 처리.
        session.invalidate();
        return "redirect:/";
    }
}
