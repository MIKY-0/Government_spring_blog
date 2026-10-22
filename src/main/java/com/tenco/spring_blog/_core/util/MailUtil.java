package com.tenco.spring_blog._core.util;

import java.security.SecureRandom;
import java.util.Random;

public class MailUtil {
    // 시큐리티 - 예측하기 어려운 난수 생성기(보안용도)
    private static final SecureRandom RANDOM = new SecureRandom();

    // 6자리 인증번호 생성(100000 ~ 999999)
    public static String generateCode(){
        int code = 100000 + RANDOM.nextInt(900000); // nextInt(a) : a는 랜덤생성할 값의 범위. (0부터 a - 1까지)
        return String.valueOf(code);
    }
}
