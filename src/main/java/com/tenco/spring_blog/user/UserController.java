package com.tenco.spring_blog.user;

import com.tenco.spring_blog._core.error.UnauthorizedException;
import com.tenco.spring_blog._core.util.Define;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


@Controller // IoC (제어의 역전) 싱글톤 패턴으로 관리됨.
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    private final KakaoLoginService kakaoLoginService;

    // 카카오 인가코드 요청 페이지로.
    @GetMapping("/kakao-login")
    public String kakaoLogin()  {return "redirect:" + kakaoLoginService.getAuthorizUrl();}


    // 흐름. 인가 코드 받기 --> 액세스 토큰 요청 --> 사용자 정보 요청 --> 사용자 정보 받기
    @GetMapping("/oauth/kakao")
    public String kakaoCallback(@RequestParam(name = "code" , required = false) String code , HttpSession session) {
        SessionUserDto sessionUserDto = kakaoLoginService.login(code);
        session.setAttribute(Define.SESSION_USER , sessionUserDto);

        return "redirect:/";
    }

    // 휴대폰 인증
    @GetMapping("/user/sms")
    public String certifySms(){
        return "user/sms-form";
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
    public String joinAction(UserRequest.JoinDto joinDto , HttpSession session) {
            // 1. 유효성 검사.
            joinDto.validate();

            // 이메일 인증 완료된 값 꺼내오기.
            String verifiedEmail = (String) session.getAttribute(Define.VERIFIED_EMAIL);
            userService.join(joinDto , verifiedEmail);

            // 가입 완료 후 임시 저장해둔 세션의 이메일 정보 제거. (회원가입할 때만 인증이메일 체크)
            session.removeAttribute(Define.VERIFIED_EMAIL);

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
    public String update(HttpSession session , UserRequest.UpdateDto updateDto){

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
