package com.example.thisplay.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {


    @GetMapping("/login")
    public String loginPage() {
        return "로그인 엔드포인트입니다. POST /login 으로 요청을 보내세요.";
    }
}