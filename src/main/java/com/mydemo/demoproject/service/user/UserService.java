package com.mydemo.demoproject.service.user;

import com.mydemo.demoproject.Entity.UserEntity;
import com.mydemo.demoproject.Repository.admin.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
//hello this pull
import java.util.List;
import java.util.Optional;
@Service
public class UserService{

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;



    public  String  addUser(UserEntity userEntity , Model model,String referralId)
    {
        Optional<UserEntity> varifyUsername  =userRepo.findByusername(userEntity.getUsername());
        Optional<UserEntity> varifyEmail= userRepo.findByemail(userEntity.getEmail());
        Optional<UserEntity>varifyContact= Optional.ofNullable(userRepo.findByContact(userEntity.getContact()));

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
        else if(varifyContact.isPresent()){

        model.addAttribute("errorMessage","Contact already exists");
        return "user/signup";
        }

        else {
            userEntity.setNewUserReferral(referralId);
            userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
            userRepo.save(userEntity);
            return  "user/login";
        }

    }


    public Optional<UserEntity> findAll() {
        return userRepo.findByUsernameNot("admin");
    }

    public List<UserEntity> getAll(){
      return   userRepo.findAll();
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


    public UserEntity findByUserContact(Long contact){
     return userRepo.findByContact(contact);
    }



    public  UserEntity findByReferralCode(String referralCode){

    return userRepo.findByNewUserReferral(referralCode);
    }
}

