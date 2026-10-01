package com.tenco.spring_blog.controller;

import org.h2.engine.Mode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller // IoC (제어의 역전) 싱글톤 패턴으로 관리됨.
public class UserController {
    // GET http://localhost:8080/join
    @GetMapping("/join")
    public String joinForm(){

        // templates/ <-- 콘텐츠 루트 경로
        return "user/join-form";
    }

    // GET http://localhost:8080/login
    @GetMapping("/login")
    public String loginForm(){

        return "user/login-form";
    }

    // GET http://localhost:8080/user/update
    @GetMapping("/user/update")
    public String updateForm(Model model){
        model.addAttribute("user", Map.of("username", "김민수", "email", "a@naver.com"));

        return "user/update-form";
    }

    // GET http://localhost:8080/logout
    @GetMapping("/logout")
    public String logoutForm(){

        return "redirect:/";
    }
}
