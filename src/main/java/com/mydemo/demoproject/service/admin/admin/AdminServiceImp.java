package com.mydemo.demoproject.service.admin.admin;

import com.mydemo.demoproject.Entity.UserEntity;
import com.mydemo.demoproject.Repository.admin.AdminRepo;
import com.mydemo.demoproject.security.SecurityImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class AdminServiceImp implements UserDetailsService {
   @Autowired
   AdminRepo adminRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> user   =   adminRepo.findByusername(username);
        //intercept and handle otp login
//        if(userInfo.get().isVerified()){
//            String password = userInfo.get().getPassword();
//        }
        if (!user.get().isEnable()){
            System.out.println(username+" is blocked");
            throw new UsernameNotFoundException(username+" is blocked");
        }
      else {
            return user.map(SecurityImp::new)
                    .orElseThrow(() -> new UsernameNotFoundException("username not found " + username));
        }
    }




}
