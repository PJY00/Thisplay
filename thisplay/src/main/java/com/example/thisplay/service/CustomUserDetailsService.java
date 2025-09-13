package com.example.thisplay.service;

import com.example.thisplay.DTO.CustomUserDetails;
import com.example.thisplay.Entity.UserEntity;
import com.example.thisplay.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor

public class CustomUserDetailsService  implements UserDetailsService {
    private final UserRepository userRepository;

    //nickname을 기준으로 DB에서 유저를 찾음
    @Override
    public UserDetails loadUserByUsername(String nickname) throws UsernameNotFoundException {

        UserEntity userData = userRepository.findByNickname(nickname); //DB에서 닉네임으로 사용자 조회
        if (userData == null) {
            throw new UsernameNotFoundException("User not found with nickname: " + nickname);
        }
        return new CustomUserDetails(userData);
    }
}