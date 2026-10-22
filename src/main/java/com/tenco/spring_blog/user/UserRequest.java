package com.tenco.spring_blog.user;

import com.tenco.spring_blog._core.error.Exception400;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

public class UserRequest {
    @Data
    // 이메일 인증 요청 DTO(발송 , 확인 API에서 함께 사용)
    public static class EmailCheckDto{
        private String email; // 이메일 주소.(1번째 요청시 사용)
        private String code; // 인증 번호(2번째 요청시 사용)

        public void validate(){
            if(email == null || email.trim().isEmpty()) throw new Exception400("이메일을 입력해주세요");
            if(!email.contains("@")) throw new Exception400("올바른 이메일 형식이 아닙니다");
        }

        // 인증번호 확인 API에서 추가로 호출할 예정.
        public void validateCode() {
            validate();
            if(code == null || code.trim().isEmpty()) throw new Exception400("인증번호를 입력해주세요");

        }
    }

    // 회원정보 수정용 DTO
    @Data
    public static class UpdateDto {
        private String password;
        private MultipartFile profileImage;  // 새 프로필 이미지(선택사항)

        // 비밀번호를 입력했는지 (null , "" , " " , '공백만 입력' 모두 --> 입력안함으로 볼 예정.)
        public boolean hasPassword() { // 비밀번호 입력시 true.
            return password != null && !password.isBlank();
        }

        // 새 프로필 사진을 골랐는지.
        public boolean hasProfileImage(){
            // 새 파일 선택 시 --> true 반환.
            return profileImage != null && !profileImage.isEmpty();
        }

        public void validate() {
            if(!hasPassword() && !hasProfileImage()) throw new Exception400("변경할 패스워드나 프로필 사진을 입력해주세요");
        }
    }


    @Data
    public static class JoinDto {
        private String username;
        private String password;
        private String email;
        private MultipartFile profileImage;  // 프로필 이미지 (선택사항)


        // 회원가입시 데이터 검증 메서드.
        public void validate() {
            if(username == null || username.trim().isEmpty()) throw new Exception400("사용자명은 필수입니다");
            if(password == null || password.trim().isEmpty()) throw new Exception400("패스워드는 필수입니다");
            if(email == null || email.trim().isEmpty()) throw new Exception400("이메일은 필수입니다");

            // 간단하게 이메일 형식 검증.
            if(!email.contains("@")) throw new Exception400("올바른 이메일 형식이 아닙니다.");
        }


        // DTO에서 User엔티티로 변환하는 메서드.
        // 계층 간 데이터 변환을 명확하게 분리하는것이 좋다.(DTO <--> Entity)
        public User toEntity(String profileImageFilename , String encodePassword) {
            return User.builder()
                    .username(username)
                    .password(encodePassword)
                    .email(email)
                    .profileImage(profileImageFilename)
                    .build();
        }
    }


    @Data
    public static class LoginDto {
        private String username;
        private String password;

        public void validate() {
            if(username == null || username.trim().isEmpty()) throw new Exception400("사용자명은 필수입니다");
            if(password == null || password.trim().isEmpty()) throw new Exception400("패스워드는 필수입니다");
        }
    }
}
