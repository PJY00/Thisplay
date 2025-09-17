package com.example.thisplay.common.Auth.Controller;


import com.example.thisplay.common.Auth.DTO.JoinDTO;
import com.example.thisplay.common.Auth.service.JoinService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
public class JoinController {
    public final JoinService joinService;

    @PostMapping("/join")
    public Map<String, Object> join(@RequestBody JoinDTO joinDTO) {
        String result = joinService.join(joinDTO); //JoinDTO 객체로 변환

        //응답을 JSON 형식으로 구성
        Map<String, Object> response = new HashMap<>();
        response.put("valid", result.equals("회원가입 성공"));
        response.put("message", result);
        response.put("nickname", joinDTO.getNickname());

        return response;
    }
}