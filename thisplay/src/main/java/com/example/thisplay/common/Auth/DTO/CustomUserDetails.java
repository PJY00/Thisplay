package com.example.thisplay.common.Auth.DTO;

import com.example.thisplay.common.Auth.Entity.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class CustomUserDetails implements UserDetails {
    private final UserEntity userEntity;

    // 생성자로 주입->객체 생성
    public CustomUserDetails(UserEntity userEntity) {
        this.userEntity = userEntity;
    }


    //필요하면 원본 객체를 그대로 가져옴
    public UserEntity getUserEntity() {
        return userEntity;
    }

    //사용자의 권한을 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(() -> userEntity.getRole());
        return collection;
    }

    //로그인시 비밀번호 비교에 사용되는 값 반환
    @Override
    public String getPassword() {
        return userEntity.getPassword();
    }

    //로그인 아이디로 사용할 값 반환
    @Override
    public String getUsername() {
        return userEntity.getNickname();
    }

    //계정 만료 여부 확인
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    //항상 로그인 가능 하도록
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    //비밀번호 만료되지 않았는지
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    //계정 항상 활성화
    @Override
    public boolean isEnabled() {
        return true;
    }
}
