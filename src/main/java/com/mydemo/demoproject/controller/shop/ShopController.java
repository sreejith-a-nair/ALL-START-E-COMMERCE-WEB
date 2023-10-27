package com.mydemo.demoproject.controller.shop;

import com.mydemo.demoproject.Entity.*;
import com.mydemo.demoproject.Entity.enumlist.BannerType;
import com.mydemo.demoproject.service.admin.brand.BrandService;
import com.mydemo.demoproject.service.admin.cartegory.CategoryService;
import com.mydemo.demoproject.service.admin.offer.OfferService;
import com.mydemo.demoproject.service.admin.product.BannerService;
import com.mydemo.demoproject.service.admin.product.ImageService;
import com.mydemo.demoproject.service.admin.product.ProductService;
import com.mydemo.demoproject.service.shop.ShopService;
import com.mydemo.demoproject.service.user.UserService;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.data.web.PageableDefault;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/shop")
public class ShopController {

    @Autowired
    ShopService shopService;

    @Autowired
    ProductService productService;
    @Autowired
    CategoryService categoryService;


    @Autowired
    BrandService brandService;
    @Autowired
    UserService userService;

    @Autowired
    OfferService offerService;

    @Autowired
    BannerService bannerService;



    /*ShopView*/
    @GetMapping("/view")
    public String shopView() {
        return "shop/shopView";

    }

    @GetMapping("/navBar")
    public  String showNavContent(@RequestParam(value = "category", required = false) String category, Model model){


       List<Brand>brands= brandService.findAll();
       List<ProductInfo>productList=productService.loadAllProduct();

        if (category != null) {
            productList = productList.stream()
                    .filter(product -> product.getCategory().equals(category))
                    .collect(Collectors.toList());
        }

        model.addAttribute("productList",productList);
        model.addAttribute("brands",brands);
        return"shop/base";
    }





    /*PRODUCT SEARCH*/

    @GetMapping(value = "/search", params = "keyword")
    public String productSearch(@RequestParam @Param("keyword") String keyword, Model model,
                                @RequestParam(name = "pageSize", defaultValue = "6") int pageSize,
                                @PageableDefault(size = 6) Pageable pageable) {
        Page<ProductInfo> productInfo = productService.searchProduct(keyword, pageable);
        List<Brand> brands = brandService.loadAllBrand();
        List<CategoryInfo> category = categoryService.findAll();

        model.addAttribute("pageSize", pageSize);
        model.addAttribute("brands", brands);
        model.addAttribute("category", category);
        model.addAttribute("productInfo", productInfo);
        return "shop/filterProduct";
    }
    /*SEARCH END*/



    /*SHOW ALL PRODUCT LIST*/


    @GetMapping("/showAllProductsList")
    public String getProduct(@RequestParam(name = "pageSize", defaultValue = "9") int pageSize,
                             @PageableDefault(size = 9) Pageable pageable, Model model) {
        List<Brand> brands = brandService.loadAllBrand();
        List<CategoryInfo> category = categoryService.findAll();


        Page<ProductInfo> productInfo = productService.loadAllProducts(PageRequest.of(pageable.getPageNumber(), pageSize));
        List<ProductInfo> productInfoList = productService.loadAllProduct();

        for (ProductInfo product : productInfoList) {
            List<Offer> productOffers = offerService.getProductOffers(product);
            List<Offer> categoryOffers = offerService.getCategoryOffers(product.getCategory());

            int productOff = 0;
            int categoryOff = 0;


            if (!productOffers.isEmpty()) {
                // Product-wise offers
                productOff = productOffers.get(0).getCategoryOffPercentage();
            } else if (!categoryOffers.isEmpty()) {
                // Category-wise offers
                categoryOff = categoryOffers.get(0).getCategoryOffPercentage();
            }


            if (productOff > categoryOff) {
                float discountedPrice = product.getPrice();
                int offerPercentage = productOff;

                if (offerPercentage > 0) {
                    discountedPrice = product.getPrice() - (product.getPrice() * offerPercentage / 100);
                }

                product.setDiscountedPrice(discountedPrice);

            } else {
                float discountedPrice = product.getPrice();
                int offerPercentage = categoryOff;

                if (offerPercentage > 0) {
                    discountedPrice = product.getPrice() - (product.getPrice() * offerPercentage / 100);
                }

                product.setDiscountedPrice(discountedPrice);
            }
        }

        model.addAttribute("pageSize", pageSize);
        model.addAttribute("productInfo", productInfo);
        model.addAttribute("category", category);
        model.addAttribute("brands", brands);

        return "shop/filterProduct";
    }

    /*SHOW ALL PRODUCT LIST END /*END SEARCH PAGINATION*/



    /*FILTER PRODUCT BY BRAND*/

    @GetMapping("/filterByBrand")
    public String filterByBrand(@RequestParam("selectedBrandUuid") UUID selectedBrandUuid,
                                @RequestParam(name = "pageSize", defaultValue = "6") int pageSize, Model model,
                                @PageableDefault(size = 6) Pageable pageable) {

        Page<ProductInfo> productInfo = brandService.getBrandProducts(selectedBrandUuid, pageable);

        List<CategoryInfo> category = categoryService.findAll();
        List<Brand> brands = brandService.loadAllBrand();
        List<ProductInfo> productInfoList = productService.loadAllProduct();


        float discounted;
        int offerPercentage = 0;
        for (ProductInfo product : productInfoList) {
            ProductInfo productInfo1 = product;
            CategoryInfo categories = product.getCategory();
//            List<Offer> categoryOffers = offerService.getCategoryOffers(categories);
            List<Offer> productOffers = offerService.getProductOffers(productInfo1);
            if (!productOffers.isEmpty()) {
                offerPercentage = productOffers.get(0).getCategoryOffPercentage();
                float productPrice = product.getPrice();
                discounted = productPrice - (productPrice * offerPercentage / 100);
                product.setDiscountedPrice(discounted);
            } else {
                product.setPrice(product.getPrice());
            }

        }
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("productInfo", productInfo);
        model.addAttribute("category", category);
        model.addAttribute("brands", brands);

        return "shop/filterProduct";

    }



    /*END FILTER PRODUCT BY BRANDT*/


    /*FILTER CATEGORY BY PRODUCT*/
    @GetMapping("/filterByCategory")
    public String filterByCategory(@RequestParam("selectedCategoryUuid") UUID selectedCategoryUuid,
                                   @RequestParam(name = "pageSize", defaultValue = "6") int pageSize, Model model,
                                   @PageableDefault(size = 6) Pageable pageable) {

        Page<ProductInfo> productInfo = productService.getCategoryProducts(selectedCategoryUuid, pageable);
        List<CategoryInfo> category = categoryService.findAll();
        List<Brand> brands = brandService.loadAllBrand();
        List<ProductInfo> productInfoList = productService.loadAllProduct();

        float discounted;
        int offerPercentage = 0;
        for (ProductInfo product : productInfoList) {
            CategoryInfo categories = product.getCategory();
            List<Offer> categoryOffers = offerService.getCategoryOffers(categories);

            if (!categoryOffers.isEmpty()) {
                offerPercentage = categoryOffers.get(0).getCategoryOffPercentage();
                float productPrice = product.getPrice();
                discounted = productPrice - (productPrice * offerPercentage / 100);
                product.setDiscountedPrice(discounted);
            } else {
                product.setPrice(product.getPrice());
            }

        }
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("productInfo", productInfo);
        model.addAttribute("category", category);
        model.addAttribute("brands", brands);

        return "shop/filterProduct";
    }

    /*END FILTER BY CATEGORY*/   /*END*/


    /* CATEGORY EXPLORE NAV   new update*/

    @GetMapping("/navCategoryProducts")
    public String showAllCategories(Model model) {

        List<CategoryInfo> categoryInfo = categoryService.findAll()
                .stream()
                .filter(category -> category.isEnable()) // Filters categories with enable == true
                .collect(Collectors.toList());


        /*new updates category*/
        for (CategoryInfo category : categoryInfo) {
            List<Offer> categoryOffers = offerService.getCategoryOffers(category);
            category.setOffer(categoryOffers);
        }

        for (CategoryInfo category : categoryInfo) {

            Offer offerInCategory = category.getOffer().get(0);
            System.out.println(offerInCategory);

            List<Offer> categoryOffers = offerService.getCategoryOffers(category);

            category.setOffer(categoryOffers);
            List<Brand> brands = brandService.loadAllBrand();

            model.addAttribute("categoryInfo", categoryInfo);
        }
        return "shop/CategoryProducts";

    }
    /* END CATEGORY EXPLORE NAV*/


    /* BRAND BASE PRODUCT*/
    @GetMapping("/showNavBrands")
    public String showBrands(Model  model){

        /*new update*/
        CategoryInfo categoryOffer;
        List<Banner> bannerList = bannerService.findByAllBanner()
                .stream()
                .filter(Banner::isEnable)
                .collect(Collectors.toList());


        /* Filter banners into main and other banners */
        List<Banner> offerBanner = bannerList.stream()
                .filter(banner -> BannerType.OFFER_BANNER.getDisplayName().equals(banner.getBannerName()))
                .collect(Collectors.toList());

        for (Banner  bannerName :offerBanner){

        }

        List<Brand> brands = brandService.loadAllBrand();

        model.addAttribute("offerBanner", offerBanner);
        model.addAttribute("brands",brands);
        model.addAttribute("bannerList",bannerList);
        return "shop/showBrandProduct";
    }

    /* END  BRAND BASE PRODUCT new update*/


    /*3 CATEGORY EXPLORE */
    @GetMapping("/categoryExplore/{uuid}")
    public String filterCategory(@PathVariable UUID uuid, Model model,
                                 @RequestParam(name = "pageSize", defaultValue = "6") int pageSize,
                                 @PageableDefault(size = 6) Pageable pageable) {

        Page<ProductInfo> productInfo = productService.getCategoryProducts(uuid, pageable);
//    List<CategoryInfo> category = categoryService.findAll();
        List<Brand> brands = brandService.loadAllBrand();

        /*new updates*/


        List<CategoryInfo> category = categoryService.findAll();
//            .stream()
//            .filter(category -> category.isEnable()) // Filters categories with enable == true
//            .collect(Collectors.toList());

        /*new updates category*/

        float discountedPrice;
        int offerPercentage = 0;
        for (ProductInfo product : productInfo) {
            CategoryInfo categories = product.getCategory();
            List<Offer> categoryOffers = offerService.getCategoryOffers(categories);

            if (!categoryOffers.isEmpty()) {
                offerPercentage = categoryOffers.get(0).getCategoryOffPercentage();
                float productPrice = product.getPrice();

                discountedPrice = productPrice - (productPrice * offerPercentage / 100);
                product.setDiscountedPrice(discountedPrice);
            } else {
                product.setPrice(product.getPrice());
            }
        }

        /*new updates*/
        model.addAttribute("offerPercentage", offerPercentage);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("productInfo", productInfo);
        model.addAttribute("category", category);
        model.addAttribute("brands", brands);
        return "shop/filterProduct";
    }
    /* END EXPLORE CATEGORY PRODUCTS*/

    /* FILTER PRODUCTS   PRICE FILTERING*/
    @GetMapping("/priceFilter")
    public String priceFiltering(@RequestParam(value = "priceRange", required = false) String priceRange,
                                 @RequestParam(name = "pageSize", defaultValue = "6") int pageSize,
                                 @RequestParam(name = "page", defaultValue = "0") int page,
                                 @PageableDefault(size = 6) Pageable pageable,
                                 Model model) {

        List<CategoryInfo> category = categoryService.findAll();
        List<Brand> brands = brandService.loadAllBrand();

        List<ProductInfo> productInfoList = productService.loadAllProduct();
        float discountedPrice;
        int offerPercentage = 0;
        for (ProductInfo product : productInfoList) {

            List<Offer> productOffers = offerService.getProductOffers(product);
            if (!productOffers.isEmpty()) {
                offerPercentage = 0;
                discountedPrice = product.getPrice();

                for (Offer productOffer : productOffers) {
                    int currentOfferPercentage = productOffer.getCategoryOffPercentage(); // Change this to match your field name
                    offerPercentage = currentOfferPercentage;
                }

                product.setDiscountedPrice(discountedPrice);

                float productPrice = product.getPrice();
                discountedPrice = productPrice - (productPrice * offerPercentage / 100);
                product.setDiscountedPrice(discountedPrice);
            }
        }
        if (priceRange != null) {
            String[] rangeParts = priceRange.split("-");
            float minPrice = Float.parseFloat(rangeParts[0]);
            float maxPrice = Float.parseFloat(rangeParts[1]);

            Page<ProductInfo> productInfo = productService.getProductsInPriceRange(minPrice, maxPrice, PageRequest.of(page, pageSize));


            model.addAttribute("pageSize", pageSize);
            model.addAttribute("productInfo", productInfo);
            model.addAttribute("category", category);
            model.addAttribute("brands", brands);
            return "shop/filterProduct";
        } else {
            Page<ProductInfo> productInfo = productService.loadAllProducts(PageRequest.of(pageable.getPageNumber(), pageSize));
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("productInfo", productInfo);
            model.addAttribute("category", category);
            model.addAttribute("brands", brands);
        }

        return "shop/filterProduct";

    }

    /*END FILTER PRODUCTS BY PRICE RANGE*/

    @GetMapping("/offerPercentageFilter")
    public String offerPercentageFiltering(@RequestParam(value = "offerPercentageRange", required = false) String offerPercentageRange,
                                           @RequestParam(name = "pageSize", defaultValue = "6") int pageSize,
                                           @RequestParam(name = "page", defaultValue = "0") int page,
                                           @PageableDefault(size = 6) Pageable pageable,
                                           Model model) {

        List<CategoryInfo> category = categoryService.findAll();
        List<Brand> brands = brandService.loadAllBrand();


        List<ProductInfo> productInfoList = productService.loadAllProduct();
        float discountedPrice;
        int offerPercentage = 0;
        for (ProductInfo product : productInfoList) {

            List<Offer> productOffers = offerService.getProductOffers(product);
            if (!productOffers.isEmpty()) {
                offerPercentage = 0;
                discountedPrice = product.getPrice();

                for (Offer productOffer : productOffers) {
                    int currentOfferPercentage = productOffer.getCategoryOffPercentage(); // Change this to match your field name
                    offerPercentage = currentOfferPercentage;
                }

                product.setDiscountedPrice(discountedPrice);

                float productPrice = product.getPrice();
                discountedPrice = productPrice - (productPrice * offerPercentage / 100);
                product.setDiscountedPrice(discountedPrice);
            }
        }

        if (offerPercentageRange != null) {


            String[] rangeParts = offerPercentageRange.split("-");
            int minPercentage = Integer.parseInt(rangeParts[0]);
            int maxPercentage = Integer.parseInt(rangeParts[1]);

            Page<ProductInfo> productInfo = productService.getProductsInOfferPercentageRange(minPercentage, maxPercentage, PageRequest.of(page, pageSize));

            model.addAttribute("pageSize", pageSize);
            model.addAttribute("productInfo", productInfo);
            model.addAttribute("category", category);
            model.addAttribute("brands", brands);
            return "shop/filterProduct";
        } else {
            Page<ProductInfo> productInfo = productService.loadAllProducts(PageRequest.of(pageable.getPageNumber(), pageSize));
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("productInfo", productInfo);
            model.addAttribute("category", category);
            model.addAttribute("brands", brands);
        }

        return "shop/filterProduct";
    }



    /*SINGLE PRODUCT /*SINGLE PRODUCTS SHOW*/
    @GetMapping("/singleProduct/{uuid}")
    public String showSingleProduct(@PathVariable UUID uuid, Model model) {
        Optional<ProductInfo> productInfo = productService.getProduct(uuid);
        ProductInfo product = productInfo.get();
        Long currentStock = product.getStock();

        if (product != null) {

            List<Offer> productOffer = product.getOffer();

            List<CategoryInfo> category = categoryService.findAll();

            CategoryInfo categories = product.getCategory();
            List<Offer> categoryOffers = offerService.getCategoryOffers(categories);

            List<Offer> productOff = offerService.getProductOffers(product);


            /*NEW*/

            float discountPrice = 0f;

            for (Offer productWiseOffer : productOff) {
                for (Offer categoryOffer : categoryOffers) {
                    if (productWiseOffer.getCategoryOffPercentage() > categoryOffer.getCategoryOffPercentage()) {

                        discountPrice = product.getPrice() - (product.getPrice() * productWiseOffer.getCategoryOffPercentage() / 100);
                        product.setDiscountedPrice(discountPrice);

                        model.addAttribute("productInfo", productInfo);
                        return "shop/singleproduct";
                    } else {

                        discountPrice = product.getPrice() - (product.getPrice() * categoryOffer.getCategoryOffPercentage() / 100);
                        product.setDiscountedPrice(discountPrice);

                        model.addAttribute("productInfo", productInfo);
                        return "shop/singleproduct";
                    }

                }
            }
        }

        product.setDiscountedPrice(product.getPrice());
        model.addAttribute("error", "Product not found");
        return "shop/singleproduct";
    }


            /*END*/


   /*END SINGLE PRODUCT SHOW*/

    /*Get wishlist page*/
    @GetMapping("/cart")
    public String getCart() {
        return "shop/cart";
    }


    /*Get wishlist page*/
    @GetMapping("/wishlist")
    public String getWshList() {
        return "shop/wishlist";
    }


   /*Get profile page*/
    @GetMapping("/profile")
    public String showProfile(@AuthenticationPrincipal(expression = "username")String  username,
                              Model model) {
        UserEntity user= userService.findByUsernames(username);
        System.out.println(user);
        model.addAttribute("user",user);
        return "shop/profile";
    }


    /*1. Show category*/
    @GetMapping("/showCategory")
    public String showAllCategory(Model model) {

        List<CategoryInfo> categoryInfo = categoryService.findAll()
                .stream()
                .filter(category -> category.isEnable()) // Filters categories with enable == true
                .collect(Collectors.toList());

        model.addAttribute("categoryInfo", categoryInfo);
        return "shop/shopView";
    }

    /*2. Show products 6 random products in landing page*/
    @GetMapping("/showProduct")
    public String showRandomProducts(Model model) {
        List<ProductInfo> productInfo = productService.loadAllProduct();

        model.addAttribute("productInfo", productInfo);
        return "shop/shopView";
    }

    @GetMapping("/about")
    public String showAbout(){

        return "shop/about";
    }

/*referral code*/
    @GetMapping("/referral-code")
    public String showReferralCode(Model model,@AuthenticationPrincipal(expression = "username")String  username){

        UserEntity user = userService.findByUsernames(username);
        Wallet wallet=user.getWallet();
        String userReferralCode =user.getNewUserReferral();

        model.addAttribute("userWallet",wallet);
        model.addAttribute("user",user);
        model.addAttribute("userReferralCode",userReferralCode);
        return "shop/wallet";
    }
    @GetMapping("/generateCode")
    public String generateCode(Model model,@AuthenticationPrincipal(expression = "username")String  username ){
        String referral=  shopService.getReferralCode(username);

        model.addAttribute("referral",referral);
        return "shop/wallet";

    }
    @GetMapping("/referWithMail")
    public String sendReferWithMail (@RequestParam("email") String email,
                                     @AuthenticationPrincipal(expression = "username")String  username,
                                     Model model) {


        UserEntity user = userService.findByUsernames(username);
        Wallet wallet=user.getWallet();
        String userReferralCode =user.getNewUserReferral();



        boolean validateEmail  =shopService.validateEmail(email);
        if (!validateEmail) {
            model.addAttribute("errorMessage", "Please enter a valid email address.");
            model.addAttribute("successMessage", null);
        } else {
            Optional<UserEntity> users =userService.getUserdata(username);
            UserEntity userEntity=users.get();
            String referralCode  =  userEntity.getNewUserReferral();

            String subject="Referral Message ";

            shopService.sendMail(email, subject, referralCode);
            model.addAttribute("errorMessage", null);
            model.addAttribute("successMessage", "Mail sent successfully.");
        }
        model.addAttribute("userWallet",wallet);
        model.addAttribute("user",user);
        model.addAttribute("userReferralCode",userReferralCode);
        return "shop/wallet";
//        return "redirect:/shop/referral-code";
    }



    @GetMapping("/showWalletMoney")
    public String getMoney(Model model,@AuthenticationPrincipal(expression = "username")String  username){


        shopService.getMoney(username);
       return "shop/wallet";

    }




}