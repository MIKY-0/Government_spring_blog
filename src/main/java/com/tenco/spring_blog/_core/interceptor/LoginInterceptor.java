package com.tenco.spring_blog._core.interceptor;


import com.tenco.spring_blog._core.error.Exception401;
import com.tenco.spring_blog._core.util.Define;
import com.tenco.spring_blog.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component // IoC + 싱글톤
public class LoginInterceptor implements HandlerInterceptor {
    // HandlerInterceptor인터페이스에 있는 메서드들.

    @Override
    // 컨트롤러에 들어가기 전에 동작.
    // 단, 설정 클래스에 등록되어 있고 지정한 URL패턴에 해당할 때만 동작.
    // return true : 컨트롤러 안으로 들여보냄. , false : 컨트롤러 안으로 못들어가도록.
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        /*
        요청(request)의 세션을 가져오는데 false옵션 : 세션이 없으면 새로 만들지 않고 null 반환.쿠키에 JSession이 있다면 그걸 가져옴.
        request.getSession() : 비로그인 사용자에게도 빈 세션이 생성됨.
         */
        HttpSession session = request.getSession(false);
        User sessionUser = (session == null) ? null : (User)session.getAttribute(Define.SESSION_USER);
        if(sessionUser == null) throw new Exception401("로그인 먼저 해주세요");

        return true;
    }


    @Override
    // 컨트롤러 실행 후 뷰가 렌더링되기 바로 직전에 호출.
    // 주의 : 컨트롤러에서 예외가 발생하면 호출되지 않음.
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }


    @Override
    // 요청 처리 완료된 후 즉, 뷰가 완전히 렌더링 된 후에 호출.
    // 예외가 발생해도 호출 되며 , 발생한 예외는 ex매개변수로 전달된다.
    // 단, preHandle이 true를 반환한 경우에만 호출됨.
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
