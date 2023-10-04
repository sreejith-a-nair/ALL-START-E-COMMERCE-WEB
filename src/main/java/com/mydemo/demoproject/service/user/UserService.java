package com.mydemo.demoproject.service.user;

import com.mydemo.demoproject.Entity.UserEntity;
import com.mydemo.demoproject.Repository.admin.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.Optional;
@Service
public class UserService{

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;



    public  String  addUser(UserEntity userEntity , Model model)
    {
        Optional<UserEntity> varifyUsername  =userRepo.findByusername(userEntity.getUsername());
        Optional<UserEntity> varifyEmail= userRepo.findByemail(userEntity.getEmail());

        if (varifyUsername.isPresent())
        {
            model.addAttribute("errorMessage","Username already exists");
            return "user/signup";
        }
        else if (varifyEmail.isPresent())
        {
            model.addAttribute("errorMessage","E-mail already exists");
            return "user/signup";
        }

        else {
            userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
            userRepo.save(userEntity);
            return  "user/login";
        }

    }


    public Optional<UserEntity> findAll() {
        return userRepo.findByUsernameNot("admin");
    }


    public  Optional<UserEntity>getUserdata(String username)
    {
       return  userRepo.findByusername(username);
    }

/*otp*/
public UserEntity findByUsernames(String username) {
    return userRepo.findByusername(username).orElse(null);
}

    public UserEntity findByPhone(String phone) {
        long contact;
        try {
            contact = Long.parseLong(phone);
        } catch (NumberFormatException e) {
            // Handle the case where the phone string is not a valid long
            return null; // or throw an exception or handle the error in a different way
        }
        return userRepo.findByContact(contact);
    }

    public UserEntity save(UserEntity userEntity) {
        return userRepo.save(userEntity);
    }
}

