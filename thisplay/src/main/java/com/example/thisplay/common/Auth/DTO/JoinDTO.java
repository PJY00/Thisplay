//회원가입 요청에서 전달되는 데이터를 담음
package com.example.thisplay.common.Auth.DTO;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class JoinDTO {
    private String nickname;
    private String password;
}