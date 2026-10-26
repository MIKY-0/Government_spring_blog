package com.tenco.spring_blog.sms;

import com.tenco.spring_blog._core.util.Define;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@Controller     @RequiredArgsConstructor
public class SmsController {
    private final SmsService smsService;

    @PostMapping("/api/sms/send")
    public ResponseEntity<?> sendCode(@RequestBody SmsRequest.PhoneCodeDto phoneCodeDto , HttpSession session) {
        phoneCodeDto.validate();
        smsService.sendVerificationCode(phoneCodeDto.getPhone() , session);
        return ResponseEntity.ok(Map.of("message", "인증코드가 전송되었습니다." + Define.CODE_EXPIRE_MINUTES + "분 안에 인증해주세요"));
    }
}
