package com.example.thisplay.global.util;

import java.util.regex.Pattern;

public class PasswordValidator {

    // 비밀번호 정규식: 8~20자, 대문자/소문자/숫자/특수문자 모두 포함
    private static final String PASSWORD_PATTERN =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$";

    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    //비밀번호를 검사
    public static boolean isValid(String password) {
        return pattern.matcher(password).matches();
    }
}