package com.mydemo.demoproject.controller.loginOtp;

import com.mydemo.demoproject.Entity.*;
import com.mydemo.demoproject.Entity.enumlist.BannerType;
import com.mydemo.demoproject.Repository.admin.ImageRepository;
import com.mydemo.demoproject.Repository.admin.ProductRepo;
import com.mydemo.demoproject.service.admin.brand.BrandService;
import com.mydemo.demoproject.service.admin.cartegory.CategoryService;
import com.mydemo.demoproject.service.admin.offer.OfferService;
import com.mydemo.demoproject.service.admin.product.BannerService;
import com.mydemo.demoproject.service.admin.product.ImageService;
import com.mydemo.demoproject.service.admin.product.ProductService;
import com.mydemo.demoproject.service.otp.OtpService;
import com.mydemo.demoproject.service.shop.ShopService;
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

import java.util.*;
import java.util.stream.Collectors;


@Controller
    public class LoginController {

    @Autowired
    OtpService otpService;

    @Autowired
    UserService userService;

    @Autowired
    ProductService productService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    ImageService imageService;

    @Autowired
    BannerService bannerService;

    @Autowired
    OfferService offerService;

    @Autowired
    BrandService brandService;

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

/*end*/
        if (((Collection<?>) authorities).contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return "redirect:/admin/home";
        } else {
            float discountedPrice=0f;
            UserEntity userEntity = userService.findByUsernames(username);


            Wallet wallet=new Wallet();
            wallet.setUserEntity(userEntity);


            System.out.println(userEntity);
            System.out.println(userEntity.isVerify());

            if (!userEntity.isVerify()) {
                model.addAttribute("failed", true);
                otpService.sendPhoneOtp(userEntity.getContact().toString());
                model.addAttribute("phone", userEntity.getContact().toString());

                return "user/verification";
            }
            Optional<UserEntity> users = userService.getUserdata(username);
            UserEntity user = users.orElse(null);


            /*new update*/
            CategoryInfo categoryOffer;
            List<Banner> bannerList = bannerService.findByAllBanner()
                    .stream()
                    .filter(Banner::isEnable)
                    .collect(Collectors.toList());


//            List<Banner> bannerList = bannerService.findByAllBanner()
//                    .stream()
//                    .filter(Banner::isEnable)
//                    .collect(Collectors.toList());
//


            /* Filter banners into main and other banners */
            List<Banner> offerBanner = bannerList.stream()
                    .filter(banner -> BannerType.OFFER_BANNER.getDisplayName().equals(banner.getBannerName()))
                    .collect(Collectors.toList());

            for (Banner  bannerName :offerBanner){
            }


            List<Banner> mainBanners = bannerList.stream()
                    .filter(banner -> !BannerType.OFFER_BANNER.getDisplayName().equals(banner.getBannerName()))
                    .collect(Collectors.toList());

            for (Banner  bannerName :mainBanners){
            }



            /*Image*/
            List<Image>images=imageService.findAllImage();

            int offerPercentage;
            /*enable product only show*/
            List<ProductInfo> allProductInfoList = productService.loadAllProduct()
                    .stream()
                    .filter(ProductInfo::isEnable) // Filters products with enable == true
                    .collect(Collectors.toList());

            List<CategoryInfo> categoryInfo = categoryService.findAll()
                    .stream()
                    .filter(category -> category.isEnable()) // Filters categories with enable == true
                    .collect(Collectors.toList());


            /*new updates category*/
            for (CategoryInfo category : categoryInfo) {
                List<Offer> categoryOffers = offerService.getCategoryOffers(category);
                category.setOffer(categoryOffers);
            }

            for (CategoryInfo category:categoryInfo){

             Offer offerInCategory=category.getOffer().get(0);
                System.out.println(offerInCategory);

                  List<Offer> categoryOffers = offerService.getCategoryOffers(category);

                  category.setOffer(categoryOffers);


                for( ProductInfo product:allProductInfoList)  {
                    List<Offer> productOffer = offerService.getProductOffers(product);

                    float productPrice=  product.getPrice();
                    float productDiscountPrice = productPrice;
                    for (Offer offerPercent:productOffer) {

                        offerPercentage  =offerPercent.getCategoryOffPercentage();
                        productDiscountPrice  =productDiscountPrice-(productDiscountPrice*offerPercentage)/100;

                    }
                    product.setDiscountedPrice(productDiscountPrice);
                    model.addAttribute("productDiscountPrice",productDiscountPrice);

                    product.setOffer(productOffer);
                    model.addAttribute("productDiscountPrice",productDiscountPrice);
                }
                int numberOfRandomProducts = Math.min(6, allProductInfoList.size());
                Collections.shuffle(allProductInfoList);

                List<ProductInfo> productInfoList = allProductInfoList.subList(0, numberOfRandomProducts);

                List<Brand> brands = brandService.loadAllBrand();

                  model.addAttribute("brands",brands);
                  model.addAttribute("offerBanner", offerBanner);
                  model.addAttribute("mainBanners", mainBanners);
                  model.addAttribute("images",images);
                  model.addAttribute("categoryInfo",categoryInfo);
                  model.addAttribute("productInfoList",productInfoList);
                  model.addAttribute("user", user);
                  return  "shop/shopView";
//              }

            }

            /*NEW OFFER*/
             for( ProductInfo product:allProductInfoList)  {
                     List<Offer> productOffer = offerService.getProductOffers(product);

                     float productPrice=  product.getPrice();
                     float productDiscountPrice = productPrice;
                     for (Offer offerPercent:productOffer) {

                         offerPercentage  =offerPercent.getCategoryOffPercentage();
                         productDiscountPrice  =productDiscountPrice-(productDiscountPrice*offerPercentage)/100;

                     }
                     product.setDiscountedPrice(productDiscountPrice);
                     model.addAttribute("productDiscountPrice",productDiscountPrice);

                     product.setOffer(productOffer);
                    model.addAttribute("productDiscountPrice",productDiscountPrice);
                     }
                      int numberOfRandomProducts = Math.min(6, allProductInfoList.size());
                      Collections.shuffle(allProductInfoList);

                       List<ProductInfo> productInfoList = allProductInfoList.subList(0, numberOfRandomProducts);

            List<Brand> brands = brandService.loadAllBrand();

            model.addAttribute("brands",brands);

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
        String currentUsername = String.valueOf(getCurrentUsername());
        UserEntity userEntity = userService.findByUsernames(currentUsername);

        List<ProductInfo> products=productService.findAll();
        List<CategoryInfo> categories = categoryService.findAll();

        if (!userEntity.isVerify()) {
            String phone = userEntity.getPhone();
            model.addAttribute("phone", phone);
            //send otp
            otpService.sendPhoneOtp(phone);

            return "user/verification";
        }
        else{
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

