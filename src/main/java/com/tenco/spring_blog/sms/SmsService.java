package com.tenco.spring_blog.sms;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service     @RequiredArgsConstructor
public class SmsService {

    public void sendVerificationCode(String phone, HttpSession session) {

    }
}
