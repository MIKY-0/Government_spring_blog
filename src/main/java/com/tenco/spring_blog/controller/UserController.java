package com.tenco.spring_blog.controller;

import com.sun.nio.sctp.IllegalReceiveException;
import com.tenco.spring_blog.user.User;
import com.tenco.spring_blog.user.UserPersistRepository;
import com.tenco.spring_blog.user.UserRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.h2.engine.Mode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@Controller // IoC (제어의 역전) 싱글톤 패턴으로 관리됨.
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserPersistRepository userPersistRepository;

    // 회원가입.  GET   http://localhost:8080/join
    @GetMapping("/join")
    public String joinForm(){
        return "user/join-form";
    }

    @PostMapping("/join")
    public String joinAction(UserRequest.JoinDto req , Model model) {

        log.info("==============회원가입 요청==============");
        log.info("사용자명 : {}" , req.getUsername());
        log.info("비밀번호 : {}" , req.getPassword());
        log.info("이메일 : {}" , req.getEmail());

        try {
            // 1. 유효성 검사.
            req.validate();

            // 2. 사용자명 중복체크.
            User existingUser = userPersistRepository.findByName(req.getUsername());
            if(existingUser != null) throw new IllegalReceiveException("이미 존재하는 사용자명입니다");

            // 3. DTO를 엔티티로 변환.
            User user = req.toEntity();

            // 4. DB에 회원정보 저장.
            // 영속화된 userEntity가 될거임.
            User userEntity = userPersistRepository.save(user);

            return "redirect:/login";

        } catch (Exception e) {
            log.error("회원가입 실패 : {} " , e.getMessage());
            model.addAttribute("errorMessage" , e.getMessage());
            return "user/join-form";
        }

    }


    // 로그인 처리. GET   http://localhost:8080/login
    @GetMapping("/login")
    public String loginForm(){

        return "user/login-form";
    }

    // 예외적으로 post요청.
    @PostMapping("/login")
    public String loginAction(UserRequest.LoginDto req , HttpSession session , Model model) {
        log.info("========로그인 요청========");
        log.info("사용자명 : {}" , req.getUsername());

        try {
            // 1. 입력 데이터 검증.
            req.validate();

            // 2. 사용자명과 비밀번호로 사용자 조회.
            User sessionUser = userPersistRepository.findByUsernameAndPassword(req.getUsername() , req.getPassword());

            // 3. 로그인 성공 / 실패 처리.
            if(sessionUser == null) {
                // 일치하는 사용자 없음.
                throw new IllegalReceiveException("사용자명 또는 비밀번호가 올바르지 않습니다.");
            }

            // mustache가 세션 값을 기본으로 읽지 않는 설정이 되어있음.
            // mustache파일에서 세션 메모리에 접근할 수 있도록 설정 추가해야함. application.yaml에

            // 4. 로그인 성공 : 세션에 사용자 정보 저장.
            // 서버는 Http요청은 각각 독립적이므로 이전에 저장한 로그인 상태를 모름.
            // 이후 요청에서도 로그인 상태를 유지하기 위해 세션에 사용자 정보 저장.
            session.setAttribute("sessionUser" , sessionUser); // header.mustache의 키값.
            log.info("로그인한 사용자 : {}" , sessionUser.getUsername());

            // 5. 메인페이지로.
            return "redirect:/";

        } catch (Exception e) {
            // 로그인 실패시 에러 메세지와 함께 로그인 폼으로 돌려보내기.
            model.addAttribute("errorMessage" , e.getMessage());
            return "user/login-form";
        }
    }


    // GET http://localhost:8080/user/update
    @GetMapping("/user/update")
    public String updateForm(Model model , HttpSession session){
        // 1. 인증 검사.
        User sessionUser = (User) session.getAttribute("sessionUser");
        if(sessionUser == null) return "redirect:/login";

        User userEntity = userPersistRepository.findById(sessionUser.getId());
        model.addAttribute("user" , userEntity);

        return "user/update-form";
    }

    @PostMapping("/user/update")
    public String update(Model model , HttpSession session , UserRequest.UpdateDto req){
        // 1. 인증검사
        User sessionUser = (User) session.getAttribute("sessionUser");
        if(sessionUser == null) return "redirect:/login";

        // 2. 권한 검사
        User userEntity = userPersistRepository.findById(sessionUser.getId());

        try {
            if(!userEntity.getId().equals(sessionUser.getId())) throw new RuntimeException("현재 로그인한 사용자와 일치안함.");
            model.addAttribute("user", userEntity);
        }catch (Exception e) {
            model.addAttribute("errorMessage" , e.getMessage());
            return "user/update-form";
        }

        // 3. 유효성 검사
        req.validate();

        // 4. 세션 동기화 : 수정된 정보를 세션에 반영.
        userPersistRepository.updateByUser(userEntity , req);

        // 5. 성공 후 메인페이지로 리다이렉트.
        return "redirect:/";
    }


    // GET http://localhost:8080/logout
    @GetMapping("/logout")
    public String logoutForm(HttpSession session){
        log.info("====로그아웃 요청====");

        // 세션 무효화 처리.
        session.invalidate();
        return "redirect:/";
    }
}
