package com.tenco.spring_blog._core.interceptor;

import com.tenco.spring_blog._core.error.Exception403;
import com.tenco.spring_blog._core.error.UnauthorizedException;
import com.tenco.spring_blog._core.util.Define;
import com.tenco.spring_blog.user.SessionUserDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

// 관리자 전용 페이지 보호
@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 확인만 해야하므로 새로운 세션을 만들면 안됨.false 필수
        HttpSession session = request.getSession(false);
        SessionUserDto sessionUserDto = (session == null) ? null : (SessionUserDto) session.getAttribute(Define.SESSION_USER);
        if(sessionUserDto == null) throw new UnauthorizedException("로그인 먼저 해주세요");
        if(!sessionUserDto.isAdmin()) throw new Exception403("관리자 전용 페이지입니다");
        return true;
    }
}
