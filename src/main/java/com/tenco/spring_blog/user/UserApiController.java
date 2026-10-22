package com.tenco.spring_blog.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController     @Slf4j      @RequiredArgsConstructor
public class UserApiController {
    private final UserService userService;
    private final MailService mailService;

    // [이메일 전송 + 인증번호 받기] POST - /api/email/send    요청 바디 : {"email" : "aa@naver.com"}
    @PostMapping("/api/email/send")
    // @RequestBody : 자바스크립트에서 받을때 JSON데이터를 Req객체로 변환해서 받음.
    public ResponseEntity<?> sendCode(@RequestBody UserRequest.EmailCheckDto emailCheckDto , HttpSession session) {
        emailCheckDto.validate();
        userService.checkEmailAvailable(emailCheckDto.getEmail());

        // SMTP 사용해서 인증번호 발송 (구글 메일 서버) --> 사용자 이메일 수신함(인증번호)
        mailService.sendVerificationCode(emailCheckDto.getEmail() , session);

        // 응답 Http메시지의 바디영역에 넣고싶은 응답을 넣음. .ok는 200상태코드로 보낸다. 헤더에도 데이터 추가하고싶다면 .ok().header().body()
        return ResponseEntity.ok( Map.of("message", "인증되었습니다"));
    }


    // [인증번호 인증]  POST - /api/email/verify  요청 바디 : {"email" : "a@naver.com" , "code" : "1234"}
    @PostMapping("/api/email/verify")
    public ResponseEntity<?> verifyCode(@RequestBody UserRequest.EmailCheckDto emailCheckDto , HttpSession session) {
        emailCheckDto.validateCode();
        boolean verified = mailService.verifyCode(emailCheckDto.getEmail() , emailCheckDto.getCode() ,session);

        if(!verified) return ResponseEntity.badRequest().body(Map.of("message", "인증번호가 일치하지 않습니다"));
        return ResponseEntity.ok().body(Map.of("message", "인증되었습니다"));
    }
}
