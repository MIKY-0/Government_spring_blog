package com.tenco.spring_blog.user;

import com.sun.nio.sctp.IllegalReceiveException;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

public class UserRequest {

    @Data
    public static class JoinDto {
        private String username;
        private String password;
        private String email;


        // 회원가입시 데이터 검증 메서드.
        public void validate() {
            if(username == null || username.trim().isEmpty()) throw new IllegalReceiveException("사용자명은 필수입니다");
            if(password == null || password.trim().isEmpty()) throw new IllegalReceiveException("패스워드는 필수입니다");
            if(email == null || email.trim().isEmpty()) throw new IllegalReceiveException("이메일은 필수입니다");

            // 간단하게 이메일 형식 검증.
            if(!email.contains("@")) throw new IllegalReceiveException("올바른 이메일 형식이 아닙니다.");
        }


        // DTO에서 User엔티티로 변환하는 메서드.
        // 계층 간 데이터 변환을 명확하게 분리하는것이 좋다.(DTO <--> Entity)
        public User toEntity() {
            return User.builder()
                    .username(username)
                    .password(password)
                    .email(email)
                    .build();
        }
    }


    @Data
    public static class LoginDto {
        private String username;
        private String password;

        public void validate() {
            if(username == null || username.trim().isEmpty()) throw new IllegalReceiveException("사용자명은 필수입니다");
            if(password == null || password.trim().isEmpty()) throw new IllegalReceiveException("패스워드는 필수입니다");

        }
    }
}
