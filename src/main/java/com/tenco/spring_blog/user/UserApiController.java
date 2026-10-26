package com.tenco.spring_blog.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tenco.spring_blog._core.error.Exception400;
import com.tenco.spring_blog._core.util.Define;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        return ResponseEntity.ok( Map.of("message", "인증번호가 발송되었습니다." +
                Define.CODE_EXPIRE_MINUTES + "분 안에 인증해주세요"));
    }


    // [인증번호 인증]  POST - /api/email/verify  요청 바디 : {"email" : "a@naver.com" , "code" : "1234"}
    @PostMapping("/api/email/verify")
    public ResponseEntity<?> verifyCode(@RequestBody UserRequest.EmailCheckDto emailCheckDto , HttpSession session) {
        emailCheckDto.validateCode();
        boolean verified = mailService.verifyCode(emailCheckDto.getEmail() , emailCheckDto.getCode() ,session);

        if(!verified) return ResponseEntity.badRequest().body(Map.of("message", "인증번호가 일치하지 않습니다"));
        return ResponseEntity.ok().body(Map.of("message", "인증되었습니다"));
    }

    // 이 컨트롤러에서 발생한 예외는 JSON으로 별도 응답 처리.
    @ExceptionHandler(Exception400.class)
    public ResponseEntity<Map<String , String>> handleEx400(Exception400 e) {
        return ResponseEntity.badRequest().body(Map.of("message" , e.getMessage()));
    }

    // 메일 발송 실패 등 그 밖의 오류(500).
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String , String>> handleEx500(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "메일 발송중 오류 발생"));
    }


    // POST http://localhost:8080/api/point/charge
// 요청 바디 :  {"amount" : 1000}
    @PostMapping("/api/point/charge")
// json 바디에 데이터를 추출하려면 (약속)
    public ResponseEntity<?> chargePoint(@RequestBody UserRequest.PointChargeDto chargeDto, HttpSession session) {
        // 1. 인증검사
        SessionUserDto sessionUserDto = (SessionUserDto) session.getAttribute(Define.SESSION_USER);
        if (sessionUserDto == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "로그인이 필요합니다"));
        }

        // 2. 유효성 검사
        chargeDto.validate();

        // 3. 서비스단에 위임
        int point = userService.chargePoint(sessionUserDto.getId(), chargeDto.getAmount());

        // 4. 응답 처리
        return ResponseEntity.ok(Map.of("message", "포인트가 충전되었습니다", "point", point));
    }

}
