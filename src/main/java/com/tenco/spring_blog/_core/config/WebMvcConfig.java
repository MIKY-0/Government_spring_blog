package com.tenco.spring_blog._core.config;

import com.tenco.spring_blog._core.interceptor.IpBlockInterceptor;
import com.tenco.spring_blog._core.interceptor.LoginInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration // 스프링 설정 클래스임을 표시.(bean으로도 등록됨.)
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
    private final LoginInterceptor loginInterceptor;
    private final IpBlockInterceptor ipBlockInterceptor;

    @Override
    // 내가 정의한 인터셉터를 설정 클래스로 등록할 수 있음.
    public void addInterceptors(InterceptorRegistry registry) {
        // LoginInterceptor를 시스템에 등록.

        registry.addInterceptor(ipBlockInterceptor)
                        .addPathPatterns("/");


        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/user/**" , "/board/**") // 인터셉터가 동작할 때 URL 패턴 지정. /user , /board 아래 모든 경로 가능.
                .excludePathPatterns("/board/list" , "/board/{id:\\d+}");  // 인터셉터에서 제외할 URL 패턴 지정. \\d+는 정규표현식.
        // 1개 이상의 숫자 의미. 예) /board/1 , /board/123 (상세보기)은 로그인 없어도 접근가능.
        // /board/1/update처럼 뒤에 (/update같이)경로가 더 붙으면 제외대상에서 빠짐.

    }
}
