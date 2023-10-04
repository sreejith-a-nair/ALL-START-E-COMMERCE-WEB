package com.mydemo.demoproject.security;

import com.mydemo.demoproject.Entity.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


public class SecurityImp implements UserDetails{



    private String username;
    private String password;


/*    private String email;
    private Long contact;*/

    private List<GrantedAuthority> authorities;


    public SecurityImp(UserEntity userEntity) {

        username = userEntity.getUsername();
        password = userEntity.getPassword();
//        email = userdata.getEmail() ;
//        contact = userdata.getContact();

        authorities= Arrays.stream(userEntity.getRole().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

    }



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
