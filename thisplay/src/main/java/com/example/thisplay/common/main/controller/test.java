package com.example.thisplay.common.main.controller;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/test")
public class test {
    @GetMapping("/jwt")
    public String jwtTest() {
        return "JWT 인증 성공! 접근 가능";
    }
}
