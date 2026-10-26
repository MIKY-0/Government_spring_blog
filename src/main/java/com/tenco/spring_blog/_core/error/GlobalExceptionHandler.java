package com.tenco.spring_blog._core.error;

import com.tenco.spring_blog._core.util.Define;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// 모든 컨트롤러에서 발생하는 예외를 이 클래스에서 처리(중앙 집중화)
@ControllerAdvice   // IoC
@Slf4j
public class GlobalExceptionHandler {
    // 특정 예외 타입이 발생했을 때 실행될 메서드로 지정. Exception400 예외 발생시 여기로.
    @ExceptionHandler(Exception400.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String ex400(Exception400 e , HttpServletRequest req , Model model) {
        log.warn("==== 400 Bad Request 에러 발생 ====");
        log.warn("요청 URL : {}" , req.getRequestURL());
        log.warn("에러 메세지 : {}" , e.getMessage());
        log.warn("예외 클래스 : {}" , e.getClass().getSimpleName());

        addSessionUserDto(req , model); // 추가 (헤더에 sessionUserDto 담기)
        model.addAttribute("msg", e.getMessage());

        return "err/400";
    }

    // redirect 페이지로 이동시키려면 ex401로.
    @ExceptionHandler(Exception401.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String ex401(Exception401 e , HttpServletRequest req , RedirectAttributes rttr) {
        log.warn("==== 401 Unauthorized 에러 발생 ====");
        log.warn("요청 URL : {}" , req.getRequestURL());
        log.warn("인증 오류 : {}" , e.getMessage());
        log.warn("예외 클래스 : {}" , e.getClass().getSimpleName());

        rttr.addFlashAttribute("errorMessage", e.getMessage());

        return "redirect:/login";
    }


    // redirect가 아닌 로그인페이지로 바로 보내려면 exUnauthorized로.
    @ExceptionHandler(UnauthorizedException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String exUnauthorized(UnauthorizedException e , HttpServletRequest req) {
        log.warn("==== 401(Data 반환) Unauthorized 에러 발생 ====");
        log.warn("요청 URL : {}" , req.getRequestURL());
        log.warn("인증 오류 : {}" , e.getMessage());
        log.warn("예외 클래스 : {}" , e.getClass().getSimpleName());

        String msg = e.getMessage().replace("'" , "\\'"); // 따옴표 깨짐 방지.

        return """
                <script>
                    alert('%s');
                    location.href = "/login";
                </script>
                """.formatted(msg);
    }


//    @ExceptionHandler(Exception403.class)
//    public String ex403(Exception403 e , HttpServletRequest req) {
//        log.warn("==== 403 Forbidden 에러 발생 ====");
//        log.warn("요청 URL : {}" , req.getRequestURL());
//        log.warn("권한 오류 : {}" , e.getMessage());
//        log.warn("예외 클래스 : {}" , e.getClass().getSimpleName());
//
//
//        return "err/403";
//    }

    @ExceptionHandler(Exception403.class)
    @ResponseBody // return을 http 응답바디에 넣음. 이 return과 Http설정 등을 보고 Spring이 content-type을 text 할지 , html 할지 정함.
    @ResponseStatus(HttpStatus.FORBIDDEN) // 상태코드 403 지정(없으면 200으로 나감)
    public String ex403(Exception403 e , HttpServletRequest req ) {
        log.warn("==== 403 Forbidden 에러 발생 ====");
        log.warn("요청 URL : {}" , req.getRequestURL());
        log.warn("권한 오류 : {}" , e.getMessage());
        log.warn("예외 클래스 : {}" , e.getClass().getSimpleName());

        String msg = e.getMessage().replace("'" , "\\'"); // 따옴표 깨짐 방지.

        return """
                <script>
                    alert('%s');
                    history.back();
                </script>
                """.formatted(msg);
    }


    @ExceptionHandler(NotEnoughPointException.class)
    @ResponseBody // return을 http 응답바디에 넣음. 이 return과 Http설정 등을 보고 Spring이 content-type을 text 할지 , html 할지 정함.
    public String exNotEnoughPointException(NotEnoughPointException e , HttpServletRequest req ) {
        log.warn("==== 포인트 부족 에러 발생 ====");
        log.warn("요청 URL : {}" , req.getRequestURL());
        log.warn("권한 오류 : {}" , e.getMessage());
        log.warn("예외 클래스 : {}" , e.getClass().getSimpleName());

        String msg = e.getMessage().replace("'" , "\\'"); // 따옴표 깨짐 방지.

        return """
                <script>
                    alert('%s');
                    location.href = '/user/detail';
                </script>
                """.formatted(msg);
    }

    @ExceptionHandler(Exception404.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String ex404(Exception404 e , HttpServletRequest req , Model model) {
        log.warn("==== 404 Not Found 에러 발생 ====");
        log.info("요청 URL : {}" , req.getRequestURL());
        log.info("리소스 오류 : {}" , e.getMessage());
        log.info("예외 클래스 : {}" , e.getClass().getSimpleName());

        addSessionUserDto(req , model);
        model.addAttribute("msg", e.getMessage());

        return "err/404";
    }

    @ExceptionHandler(Exception500.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String ex500(Exception500 e , HttpServletRequest req , Model model) {
        log.warn("==== 500 Internal Server Error 에러 발생 ====");
        log.error("요청 URL : {}" , req.getRequestURL());
        log.error("서버 오류 : {}" , e.getMessage());
        log.error("StackTrace : " , e);

        addSessionUserDto(req , model);
        model.addAttribute("msg", "네트워크 일시 장애");

        return "err/500";
    }

    // 기타 모든 RuntimeException 처리. (최후의 보루)
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String runtimeException(RuntimeException e , HttpServletRequest req , Model model) {
        log.warn("==== 예상치 못한 런타임 에러 ====");
        log.error("요청 URL : {}" , req.getRequestURL());
        log.error("에러 타입 : {}" , e.getClass().getSimpleName());
        log.error("에러 메세지 : {}" , e.getMessage());
        log.error("StackTrace : " , e);

        model.addAttribute("msg", "시스템 오류 발생. 관리자에게 문의해주세요");

        return "err/500";
    }


    // 데이터베이스 제약조건 위반 (FK , Unique 중복)
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT) // 409 : 현재 데이터 상태와 충돌.
    public String exDataIntegrityViolationException(DataIntegrityViolationException e , HttpServletRequest req , Model model) {
        log.warn("==== 데이터베이스 제약조건 위반 ====");
        log.error("요청 URL : {}" , req.getRequestURL());
        log.error("원인 : {}" , e.getMostSpecificCause().getMessage());  // 상세 원인(SQL , 제약조건 이름)은 개발자용 로그에만 남김.
        model.addAttribute("msg", "시스템 오류 발생. 관리자에게 문의해주세요");

        return "err/500";
    }



    // 오류 화면에서도 헤더의 로그인 메뉴가 보이도록 sessionUserDto를 model에 추가.
    private void addSessionUserDto(HttpServletRequest request , Model model) {
        HttpSession session = request.getSession(false);
        if(session != null && session.getAttribute(Define.SESSION_USER) != null) {
            model.addAttribute(Define.SESSION_USER , session.getAttribute(Define.SESSION_USER));
        }
    }

}
