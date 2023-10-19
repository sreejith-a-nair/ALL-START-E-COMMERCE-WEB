package com.mydemo.demoproject.controller.user;


import com.mydemo.demoproject.Entity.UserEntity;
import com.mydemo.demoproject.Entity.Wallet;
import com.mydemo.demoproject.service.shop.ShopService;
import com.mydemo.demoproject.service.user.UserService;
import jdk.dynalink.linker.LinkerServices;
import org.hibernate.NonUniqueResultException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.model.IModel;


import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Controller
public class UserController {
    @Autowired
    ShopService shopService;




    @Autowired
    private UserService userService;

    @GetMapping("/signup")
    public String signup() {
        return "user/signup";
    }






    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String createUser(@ModelAttribute UserEntity userEntity,
                             @RequestParam("password") String password,
                             @RequestParam("confirmPassword") String confirmPassword,
                             @RequestParam(value = "referralCode", required = false) String referralCode, Model model,
                             @AuthenticationPrincipal UserDetails userDetails) {

        System.out.println("password" + password);


        if (!password.equals(confirmPassword)) {
            model.addAttribute("errorMessage", "Password and Confirm Password do not match.");
            return "user/signup";
        }
        String email = userEntity.getEmail();
        System.out.println(email);
        String emailRegex = "^[a-z]+@gmail.com$";
        System.out.println(emailRegex);

        if (!Pattern.matches(emailRegex, email)) {

            model.addAttribute("errorMessage", "Invalid email format. Please use the format namealphabet@gmail.com.");
            model.addAttribute("userEntity", userEntity);
            return "user/signup";
        }


        Long newContact = userEntity.getContact();
        String contactString = String.valueOf(newContact);
        int length = contactString.length();
        System.out.println(length);
        if (length != 10) {
            model.addAttribute("errorMessage", "Invalid phone number.contact number contain 10 numbers ");
            return "user/signup";
        }
        Long newContactNumber = userEntity.getContact();
        List<UserEntity> existingUser = userService.getAll();
        for (UserEntity existingUsers : existingUser) {
            Long existContact = existingUsers.getContact();
            if (newContactNumber.equals(existContact)) {
                System.out.println("new number " + newContact + " exists number " + existContact);
                model.addAttribute("errorMessage", "Phone number is already exist");
                model.addAttribute("userEntity", userEntity);
                return "user/signup";
            }
        }

        String username = userEntity.getUsername();
            /*new update*/
        Wallet newUseWallet = new Wallet();
        if (referralCode != null && !referralCode.isEmpty()) {
           UserEntity referredUser = userService.findByReferralCode(referralCode);


                        String existingUsername = referredUser.getUsername();
                        Wallet userWallet = shopService.getWalletByUser(existingUsername);
                        System.out.println("old user wallet" + userWallet);
                            float referralMoney =+ 100f;
                        userWallet.setEarnedMoney(referralMoney);
                        userWallet.setTotalMoney(referralMoney);
                            shopService.saveWallet(userWallet);


                        float referralCash = 50f;
                           newUseWallet.setEarnedMoney(referralCash);
                            userEntity.setWallet(newUseWallet);
                            userWallet.setTotalMoney(referralCash);
                            shopService.saveWallet(newUseWallet);

                    }else {
                            newUseWallet.setEarnedMoney(0f);
                            newUseWallet.setTotalMoney(0f);
                            userEntity.setWallet(newUseWallet);
                            shopService.saveWallet(newUseWallet);
                    }

            System.out.println("referralCode>>>>>>>>>>>>elseeeeee>" + referralCode);
            String userReferralCode= shopService.getReferralCode(username);
            System.out.println("userReferralCode>>>>>>>>>>>>>"+userReferralCode);
            userEntity.setRole("ROLE_USER");
            String result = userService.addUser(userEntity, model, userReferralCode);

            newUseWallet.setUserEntity(userEntity);
            shopService.saveWallet(newUseWallet);

            return result;
        }


    }