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
        // Optional로 감싸져 있으므로 orElseThrow 사용
        UserEntity userData = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with nickname: " + nickname));

        return new CustomUserDetails(userData);
    }
}