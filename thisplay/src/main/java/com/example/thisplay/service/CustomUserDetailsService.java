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

    @Override
    public UserDetails loadUserByUsername(String nickname) throws UsernameNotFoundException {

        UserEntity userData = userRepository.findByNickname(nickname);
        if (userData == null) {
            throw new UsernameNotFoundException("User not found with nickname: " + nickname);
        }
        return new CustomUserDetails(userData);
    }
}