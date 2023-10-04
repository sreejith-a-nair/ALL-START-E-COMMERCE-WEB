package com.mydemo.demoproject.service.user;

import com.mydemo.demoproject.Entity.UserEntity;
import com.mydemo.demoproject.Repository.admin.UserRepo;
import com.mydemo.demoproject.security.SecurityImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImp implements UserDetailsService
{
    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> user   =   userRepo.findByusername(username);
        return user.map(SecurityImp::new)
                .orElseThrow(()-> new UsernameNotFoundException("username not found "+username));
    }
}
