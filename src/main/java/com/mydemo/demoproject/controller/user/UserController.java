package com.mydemo.demoproject.controller.user;

import com.mydemo.demoproject.Entity.UserEntity;
import com.mydemo.demoproject.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;


import java.util.Collection;
import java.util.Optional;
@Controller
public class UserController {


    @Autowired
    private UserService userService;

    @GetMapping("/signup")
    public String  signup()
    {
        return  "user/signup";
    }

    @PostMapping("/save")
    public String createUser(@ModelAttribute UserEntity userEntity, Model model)
    {
        userEntity.setRole("ROLE_USER");
        String result =userService.addUser(userEntity,model);
        return  result;
    }


}
