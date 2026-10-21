package com.tenco.spring_blog.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j      @Component
// 카카오 서버로 요청 , 응답 받는 클래스.
public class KakaoApiClient {
    // application.yaml 의 변수를 가져올때 사용.
    @Value("${oauth.kakao.client-id}")
    private String kakaoClientId;

    @Value("${oauth.kakao.client-secret}")
    private String kakaoClientSecret;

    @Value("${oauth.kakao.redirect-uri}")
    private String redirectUri;

    // 인가 요청 주소
    private static final String AUTHORIZE_URL = "https://kauth.kakao.com/oauth/authorize";

    // 액세스 토큰 요청 주소
    private static final String TOKEN_URL = "https://kauth.kakao.com/oauth/token";

    // 프로필 이미지 요청 주소
    private static final String PROFILE_URL = "https://kapi.kakao.com/v2/user/me";

    // 한번 만들면 여러 요청에서 함께 써도 안전하게 설계되어 하나만 만들어 재사용한다.
    private final RestTemplate restTemplate = new RestTemplate();

    // 1. 카카오 로그인 화면 주소 만들기(로그인 + 동의 화면)
    public String getAuthorizeUrl(){
        return AUTHORIZE_URL +
                "?client_id=" + kakaoClientId +
                "&redirect_uri=" + redirectUri +
                "&response_type=code";
    }

    // 2. 인가 코드로 액세스 토큰 요청(POST , 폼 형식)
    public UserResponse.OauthToken requestAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String , String> params = new LinkedMultiValueMap<>();

        params.add("grant_type" , "authorization_code");
        params.add("client_id" , kakaoClientId);
        params.add("redirect_uri" , redirectUri);
        params.add("code" , code);
        params.add("client_secret" , kakaoClientSecret);

        HttpEntity<MultiValueMap<String , String>> tokenRequest = new HttpEntity(params , headers);

        ResponseEntity<UserResponse.OauthToken> response= restTemplate.exchange(
                "https://kauth.kakao.com/oauth/token" , HttpMethod.POST ,
                tokenRequest , UserResponse.OauthToken.class );
        log.info("카카오 액세스 토큰 발급 완료"); // 토큰 값은 로그에 남기지 않는다.
        return response.getBody();
    }

    // 3. 액세스 토큰으로 사용자 정보 요청(GET , 인증 헤더)
    public UserResponse.KakaoUserInfo requestProfile(String accessToken){
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity request = new HttpEntity(headers);

        ResponseEntity<UserResponse.KakaoUserInfo> response = restTemplate.exchange(
                PROFILE_URL , HttpMethod.GET , request , UserResponse.KakaoUserInfo.class );
        return response.getBody();
    }
}
