package com.tenco.spring_blog.sms;

import com.tenco.spring_blog._core.error.Exception400;
import lombok.Data;

public class SmsRequest {
    @Data
    public class PhoneCodeDto {
        private String phone;
        private String code;

        public void validate(){
            if(phone == null || phone.trim().isEmpty()) throw new Exception400("핸드폰 번호를 입력해주세요");
            if(phone.contains("-")) throw new Exception400("숫자만 입력해주세요");
            if(code == null || code.trim().isEmpty()) throw new Exception400("인증코드를 입력해주세요");
        }
    }

}
