package com.mydemo.demoproject.controller.loginOtp;

import com.mydemo.demoproject.Entity.*;
import com.mydemo.demoproject.service.admin.cartegory.CategoryService;
import com.mydemo.demoproject.service.admin.product.BannerService;
import com.mydemo.demoproject.service.admin.product.ImageService;
import com.mydemo.demoproject.service.admin.product.ProductService;
import com.mydemo.demoproject.service.otp.OtpService;
import com.mydemo.demoproject.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;


@Controller
    public class LoginController {

    @Autowired
    OtpService otpService;
//
    @Autowired
    UserService userService;
//
    @Autowired
    ProductService productService;
//
    @Autowired
    CategoryService categoryService;

    @Autowired
    ImageService imageService;

    @Autowired
    BannerService bannerService;


    /*User Authentication checking*/
    @GetMapping("/login")
    public String loginPage(Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {

            return "user/login";
        }

          return  "redirect:/home";
//        return  "redirect:/";
    }

    /*Authentication role checking*/
    @GetMapping("/home")
//    @GetMapping("/")
    public String home(Authentication authentication, Model model){

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String username = authentication.getName();



        if (((Collection<?>) authorities).contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
//            System.out.println("admin  pageeeeeeeeeeeeeeeeeeeeeeeeeeeee");
            return "redirect:/admin/home";
        } else {
//           System.out.println("inside......................................................");
            UserEntity userEntity = userService.findByUsernames(username);

            System.out.println(userEntity);
            System.out.println(userEntity.isVerify());

            if (!userEntity.isVerify()) {
                model.addAttribute("failed", true);
                otpService.sendPhoneOtp(userEntity.getContact().toString());
                model.addAttribute("phone",userEntity.getContact().toString());

                return "user/verification";
            }
            Optional<UserEntity> users = userService.getUserdata(username);
            System.out.println("user>>>>>>>>>>"+users);
            UserEntity user = users.orElse(null);

           /*enable category only show*/
            List<CategoryInfo> categoryInfo = categoryService.findAll()
                    .stream()
                    .filter(category -> category.isEnable()) // Filters categories with enable == true
                    .collect(Collectors.toList());
            System.out.println("../login.home...categoryInfo"+categoryInfo);

            /*enable product only show*/
            List<ProductInfo> allProductInfoList = productService.loadAllProduct()
                    .stream()
                    .filter(ProductInfo::isEnable) // Filters products with enable == true
                    .collect(Collectors.toList());



            List<Banner> bannerList = bannerService.findByAllBanner()
                    .stream()
                    .filter(Banner::isEnable) // Filters banners with enabled == true
                    .collect(Collectors.toList());

            int numberOfRandomProducts = Math.min(6, allProductInfoList.size());
            Collections.shuffle(allProductInfoList);

            List<ProductInfo> productInfoList = allProductInfoList.subList(0, numberOfRandomProducts);


            List<Image>images=imageService.findAllImage();
            System.out.println("images link>>"+images);
//            System.out.println("productInfoList/////////"+productInfoList);

            model.addAttribute("bannerList",bannerList);
            model.addAttribute("images",images);
            model.addAttribute("categoryInfo",categoryInfo);
            model.addAttribute("productInfoList",productInfoList);
            model.addAttribute("user", user);
            return  "shop/shopView";
        }

    }


/* OTP verification for user*/
    @PostMapping("/verify")
    public String verifyUser(@RequestParam(value = "otp") String otp,
                             @RequestParam(value = "phone") String phone,
                             Model model) {

        boolean verified = otpService.verifyPhoneOtp(otp, phone);
        System.out.println(verified);
        if (verified) {
            return "redirect:/home";
        } else {
            model.addAttribute("failed", true);
            return "user/verification";

        }
    }


    /*generate otp*/
    private String generateOTP() {
        // Generate a random 6-digit OTP
        Random random = new Random();
        int otpNumber = 100_000 + random.nextInt(900_000);
        System.out.println("Otp.........................."+otpNumber);
        return String.valueOf(otpNumber);
    }

  /*get current user-name*/
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }


    @GetMapping("/")
//    @GetMapping("/home")
    public String getHomePage(Model model) {
//        System.out.println("home.............. ////////////////////////////////////////");
        String currentUsername = String.valueOf(getCurrentUsername());
        UserEntity userEntity = userService.findByUsernames(currentUsername);

        List<ProductInfo> products=productService.findAll();
        List<CategoryInfo> categories = categoryService.findAll();

        if (!userEntity.isVerify()) {
//            System.out.println("verification........................");
            String phone = userEntity.getPhone();
            model.addAttribute("phone", phone);

            //send otp
            otpService.sendPhoneOtp(phone);

            return "user/verification";

        }
        else{
//            model.addAttribute("categories", categories);
//            model.addAttribute("products", products);

//            return  "shop/shopView";
             return "redirect:/home";

        }

    }

/* checking verify or not */
    private boolean isVerify(String username) {
        UserEntity userEntity = userService.findByUsernames(username);
        if (userEntity.getUuid() != null) {
            return userEntity.isVerify();
        } else {
            return true;
        }
    }

}

