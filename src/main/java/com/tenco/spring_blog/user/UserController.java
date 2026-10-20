package com.tenco.spring_blog.user;

import com.tenco.spring_blog._core.error.UnauthorizedException;
import com.tenco.spring_blog._core.util.Define;
import jakarta.annotation.PostConstruct;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


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


    // 카카오 인가코드 요청 페이지로.
    @GetMapping("/kakao-login")
    public String kakaoLogin(){
        String url = "https://kauth.kakao.com/oauth/authorize" +
                "?client_id=" + kakaoClientId +
                "&redirect_uri=" + redirectUri +
                "&response_type=code";

        return "redirect:" + url;
    }


    // 흐름. 인가 코드 받기 --> 액세스 토큰 요청 --> 사용자 정보 요청 --> 사용자 정보 받기
    // 1. 인가 코드 받기(code)
    @GetMapping("/oauth/kakao")
    public String kakaoCallback(@RequestParam(name = "code" , required = false) String code , HttpSession session) {
        if(code == null) throw new UnauthorizedException("카카오 로그인이 취소되었습니다.");

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<UserResponse.OauthToken> tokenResponse;

        try {
            // 2. 받은 인가 코드로 토큰 요청(POST) 만들기.
            HttpHeaders tokenHeaders = new HttpHeaders();
            // Content-Type : 일반 form 형식(x-www-....)
            tokenHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String , String> tokenParams = new LinkedMultiValueMap<>();
            tokenParams.add("grant_type" , "authorization_code");
            tokenParams.add("client_id" , kakaoClientId);
            tokenParams.add("redirect_uri" , redirectUri);
            tokenParams.add("code" , code);
            tokenParams.add("client_secret" , kakaoClientSecret);

            HttpEntity<MultiValueMap<String , String>> tokenRequest = new HttpEntity(tokenParams , tokenHeaders);

            // 3. 토큰 요청 실행 후 , 응답까지 받기
            tokenResponse = restTemplate.exchange("https://kauth.kakao.com/oauth/token" , HttpMethod.POST ,
                    tokenRequest , UserResponse.OauthToken.class);


            // 4. 응답받은 액세스 토큰으로 사용자 정보 조회.
            HttpHeaders userHeaders = new HttpHeaders();

            userHeaders.setBearerAuth(tokenResponse.getBody().getAccessToken());
            userHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity userRequest = new HttpEntity(userHeaders);
            ResponseEntity<UserResponse.KakaoUserInfo> userResponse = restTemplate.exchange("https://kapi.kakao.com/v2/user/me" ,
                    HttpMethod.GET , userRequest , UserResponse.KakaoUserInfo.class);


            // 내부 로직 - 최초 소셜 로그인 사용자라면 자동 회원 가입(필요 정보가 더 있다면 추가 기입)
            //           - 이미 회원가입 되어있는 사용자라면 로그인 처리.
            UserResponse.KakaoUserInfo kakaoUserInfo = userResponse.getBody();
            SessionUserDto sessionUserDto = userService.kakaoLogin(kakaoUserInfo);
            session.setAttribute(Define.SESSION_USER , sessionUserDto);

            return "redirect:/";
        }catch (HttpClientErrorException e){
            // 카카오가 4xx를 응답한 경우.
            log.error("카카오 API 오류 : {}" , e.getResponseBodyAsString());
            throw new UnauthorizedException("카카오 로그인에 실패했습니다");
        }
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
