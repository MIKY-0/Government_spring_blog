package com.tenco.spring_blog._core.interceptor;

import com.tenco.spring_blog._core.error.Exception400;
import com.tenco.spring_blog.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

// 특정 IP를 차단하는 인터셉터 구현. 단, 여러개 가능 (조원들 IP 차단)
@Component  @Slf4j
public class IpBlockInterceptor  implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String url = request.getRemoteAddr();
        String[] blockIp = {"192.168.5.18" , "192.168.5.13" , "192.168.7.232"};

        log.info("ip 주소 : {}" , url);
        for(String ip : blockIp) if(url.equals(ip)) throw new Exception400("당신의 IP 차단됨");
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
