package com.tenco.spring_blog.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tenco.spring_blog._core.util.MyDateUtil;
import lombok.Data;

public class UserResponse {
    // 마이페이지 응답 DTO
    @Data
    public static class DetailDto{
        private Long id;
        private String username;
        private String email;
        private String profileImage; // 파일 이름(없으면 null)
        private String createdAt; // 포맷된 가입일.

        public DetailDto(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.email = user.getEmail();
            this.profileImage = user.getProfilePath();
            if(user.getCreatedAt() != null) this.createdAt = MyDateUtil.timestampFormat(user.getCreatedAt());
        }
    }

    // 회원가입 응답 DTO.
    @Data
    public static class JoinDto{
        private Long id;
        private String username;
        private String email;

        public JoinDto(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.email = user.getEmail();
        }
    }


    // 회원정보 수정 화면 응답 DTO.
    @Data
    public static class updateFormDto{
        private String username;
        private Long id;
        private String email;
        private String profileImage;


        public updateFormDto(User user) {
            this.username = user.getUsername();
            this.id = user.getId();
            this.email = user.getEmail();
            this.profileImage = user.getProfilePath();
        }
    }


    // 카카오 액세스 토큰 Dto.
    @Data
    public static class OauthToken{
        // @JsonProperty : 응답받는 필드명과 자바에서 작성한 필드명을 서로 매핑. "access_token" --> 실제 응답받은 필드명.
        @JsonProperty("access_token")
        private String accessToken;

        @JsonProperty("token_type")
        private String tokenType;

        @JsonProperty("refresh_token")
        private String refreshToken;

        @JsonProperty("expires_in")
        private Long expiresIn;

        @JsonProperty("scope")
        private String scope;

        @JsonProperty("refresh_token_expires_in")
        private Long refreshTokenExpiresIn;
    }

// 카카오 소셜 로그인 사용자 정보 DTO
    @Data
    public static class KakaoUserInfo {
        private Long id;

        @JsonProperty("connected_at")
        private String connectedAt;

        private Properties properties;

        @JsonProperty("kakao_account")
        private Object kakaoAccount;

        @Data
        public static class Properties {
            private String nickname;

            @JsonProperty("profile_image")
            private String profileImage;

            @JsonProperty("thumbnail_image")
            private String thumbnailImage;
        }
    }

}
