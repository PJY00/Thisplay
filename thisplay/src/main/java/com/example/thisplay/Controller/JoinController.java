package com.example.thisplay.Controller;


import com.example.thisplay.DTO.JoinDTO;
import com.example.thisplay.service.JoinService;
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
    public Map<String, String> join(@RequestBody JoinDTO joinDTO) {
        String result = joinService.join(joinDTO);

        Map<String, String> response = new HashMap<>();
        response.put("message", result);
        response.put("nickname", joinDTO.getNickname());
        return response;
    }
}