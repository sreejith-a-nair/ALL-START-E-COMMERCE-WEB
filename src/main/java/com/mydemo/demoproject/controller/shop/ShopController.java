package com.mydemo.demoproject.controller.shop;

import com.mydemo.demoproject.Entity.*;
import com.mydemo.demoproject.service.admin.brand.BrandService;
import com.mydemo.demoproject.service.admin.cartegory.CategoryService;
import com.mydemo.demoproject.service.admin.offer.OfferService;
import com.mydemo.demoproject.service.admin.product.ImageService;
import com.mydemo.demoproject.service.admin.product.ProductService;
import com.mydemo.demoproject.service.shop.ShopService;
import com.mydemo.demoproject.service.user.UserService;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
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
    ImageService imageService;

    @Autowired
    BrandService brandService;
    @Autowired
    UserService userService;

    @Autowired
    OfferService offerService;



    /*ShopView*/
    @GetMapping("/view")
    public String shopView() {
        return "shop/shopView";

    }

    @GetMapping("/navBar")
    public  String showNavContent(@RequestParam(value = "category", required = false) String category, Model model){

        System.out.println("hiiiiiiiiiiiiiiiiiiiiii");
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
//        List<ProductInfo> lowProducts =productService.checkStock();
        // Sort the lowProducts list by stock in ascending order
//        Collections.sort(lowProducts, Comparator.comparing(Product::getStock));
//        model.addAttribute("lowProducts", lowProducts);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("brands", brands);
        model.addAttribute("category", category);
        model.addAttribute("productInfo", productInfo);
        return "shop/filterProduct";
    }
    /*SEARCH END*/



    /*SHOW ALL PRODUCT LIST*/





//    @GetMapping("/showAllProductsList")
//    public String getProduct(@RequestParam(name = "pageSize", defaultValue = "9") int pageSize,
//                             @PageableDefault(size = 9) Pageable pageable, Model model) {
//        List<Brand>brands=brandService.loadAllBrand();
//        List<CategoryInfo> category = categoryService.findAll();
//
//        Page<ProductInfo> productInfo = productService.loadAllProducts(PageRequest.of(pageable.getPageNumber(), pageSize));
//        List<ProductInfo>productInfoList=productService.loadAllProduct();
//
//        /*new updates*/
//        float discountedPrice;
//        int offerPercentage = 0;
//
//        for (ProductInfo product:productInfoList){
//
//
//            List<Offer> productOffers =offerService.getProductOffers(product);
//            if (!productOffers.isEmpty()) {
//                 offerPercentage = 0;
//                 discountedPrice = product.getPrice();
//
//                     for (Offer productOffer:productOffers){
//                         int currentOfferPercentage = productOffer.getCategoryOffPercentage(); // Change this to match your field name
//                         offerPercentage = currentOfferPercentage;
//                     }
//
//                product.setDiscountedPrice(discountedPrice);
//
//                System.out.println(">>>>>>>>>>>>>>offerPercentage"+offerPercentage);
//                float productPrice = product.getPrice();
//                discountedPrice = productPrice - (productPrice * offerPercentage / 100);
//                product.setDiscountedPrice(discountedPrice);
//                System.out.println("discountedPrice>>>>>>>>>>>>"+discountedPrice);
////                model.addAttribute("discountedPrice",discountedPrice);
//
//            } else {
//                System.out.println("category offer");
//                float discounted;
//                int offer = 0;
//                    CategoryInfo categories = product.getCategory();
//                    List<Offer> categoryOffers = offerService.getCategoryOffers(categories);
//
//                    if (!categoryOffers.isEmpty()) {
//                        System.out.println("category offer inside if");
//                        discounted = categoryOffers.get(0).getCategoryOffPercentage();
//                        float productPrice = product.getPrice();
//
//                        discounted= productPrice - (productPrice * offerPercentage / 100);
//                        product.setDiscountedPrice(discounted);
//                    } else {
//                        product.setPrice(product.getPrice());
//                    }
//            }
//
//        }
//        System.out.println("offerPercentage>out side if>>>"+offerPercentage);
//        /*end */
//
//        model.addAttribute("pageSize", pageSize);
//        model.addAttribute("productInfo", productInfo);
//        model.addAttribute("category",category);
//        model.addAttribute("brands",brands);
//        return "shop/filterProduct";
//}

//    @GetMapping("/showAllProductsList")
//    public String getProduct(@RequestParam(name = "pageSize", defaultValue = "9") int pageSize,
//                             @PageableDefault(size = 9) Pageable pageable, Model model) {

    @GetMapping("/showAllProductsList")
    public String getProduct(@RequestParam(name = "pageSize", defaultValue = "9") int pageSize,
                             @PageableDefault(size = 9) Pageable pageable, Model model) {
        System.out.println("/////////hello");
        List<Brand> brands = brandService.loadAllBrand();
        System.out.println("*******" + brands);
        List<CategoryInfo> category = categoryService.findAll();


        Page<ProductInfo> productInfo = productService.loadAllProducts(PageRequest.of(pageable.getPageNumber(), pageSize));
        List<ProductInfo> productInfoList = productService.loadAllProduct();

        for (ProductInfo product : productInfoList) {
            List<Offer> productOffers = offerService.getProductOffers(product);
            List<Offer> categoryOffers = offerService.getCategoryOffers(product.getCategory());

            int productOff = 0;
            int categoryOff = 0;

//            if (productOffers.isEmpty() && categoryOffers.isEmpty()) {
//
////                product.getDiscountedPrice()   = product.getPrice();
//                product.setDiscountedPrice(product.getPrice());
//
//             else {

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
//                                @RequestParam(name = "pageSize", defaultValue = "6") int pageSize,
//                                @RequestParam(name = "page", defaultValue = "0") int page,
//                                @PageableDefault(size = 6) Pageable pageable,
//                                Model model) {


        Page<ProductInfo> productInfo = brandService.getBrandProducts(selectedBrandUuid, pageable);
        System.out.println("Brand products :::::::" + productInfo);
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
                System.out.println("Offer percent by products >>>>>>>" + offerPercentage);
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
        System.out.println("Category products :::::::" + productInfo);
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
                System.out.println("Offer percent by category >>>>>>>" + offerPercentage);
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
        System.out.println("../Explore category.home...categoryInfo()>>>" + category);
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

        System.out.println("../login.home...categoryInfo()>>>>" + category);
        System.out.println("offerPercntge>explore category>" + offerPercentage);
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

                System.out.println(">>>>>>>>>>>>>>offerPercentage" + offerPercentage);
                float productPrice = product.getPrice();
                discountedPrice = productPrice - (productPrice * offerPercentage / 100);
                product.setDiscountedPrice(discountedPrice);
                System.out.println("discountedPrice>>>>>>>>>>>>" + discountedPrice);
            }
        }
        System.out.println("PRICE RANGE PRODUCTS ONLY :    " + priceRange);
        if (priceRange != null) {
            String[] rangeParts = priceRange.split("-");
            float minPrice = Float.parseFloat(rangeParts[0]);
            float maxPrice = Float.parseFloat(rangeParts[1]);

            // Load all products matching the price range
            Page<ProductInfo> productInfo = productService.getProductsInPriceRange(minPrice, maxPrice, PageRequest.of(page, pageSize));
            System.out.println("PRICE RANGE PRODUCTS ONLY :    " + productInfo);


            model.addAttribute("pageSize", pageSize);
            model.addAttribute("productInfo", productInfo);
            model.addAttribute("category", category);
            model.addAttribute("brands", brands);
            return "shop/filterProduct";
        } else {
            // No price range selected, show all products
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

                System.out.println(">>>>>>>>>>>>>>offerPercentage" + offerPercentage);
                float productPrice = product.getPrice();
                discountedPrice = productPrice - (productPrice * offerPercentage / 100);
                product.setDiscountedPrice(discountedPrice);
                System.out.println("discountedPrice>>>>>>>>>>>>" + discountedPrice);
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
            // No offer percentage range selected, show all products
            Page<ProductInfo> productInfo = productService.loadAllProducts(PageRequest.of(pageable.getPageNumber(), pageSize));
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("productInfo", productInfo);
            model.addAttribute("category", category);
            model.addAttribute("brands", brands);
        }

        return "shop/filterProduct";
    }

    /*OFFER FILTERING*/



    /*FILTER #30% OFF  PRODUCT*/

//    @GetMapping("/filterSpecialOffer")
//    public String getSpecialOfferProducts( @RequestParam(name = "value", required = false) Integer value,
//                                           @RequestParam(name = "pageSize", defaultValue = "6") int pageSize,
//                                           @RequestParam(name = "page", defaultValue = "0") int page,
//                                           @PageableDefault(size = 6) Pageable pageable,
//                                           Model model) {
//
//        Page<ProductInfo> productInfo = productService.loadAllProducts(PageRequest.of(pageable.getPageNumber(), pageSize));
//
//        List<CategoryInfo> category = categoryService.findAllCategory();
//        for (CategoryInfo categoryInfo:category){
//                   List<Offer>  offerList =offerService.getCategoryOffers(categoryInfo);
//                   for (Offer allOff:offerList){
//
//                        if(value==allOff.getCategoryOffPercentage()){
//                            System.out.println("30% off  only "+allOff.getCategoryOffPercentage());
//                            allOff.getUuid();
//                            productService.
//                        }
//                   }
//        }
//        System.out.println("haaaaaaaaaaaaaa22222222222222222222222222222222222222222222");
//        List<Brand> brands = brandService.loadAllBrand();
//        System.out.println("haaaaaaaaaaaaaa3333333333333333333333333333333333333333");
//        for (ProductInfo productInfo1 : productInfo) {
//            System.out.println("444444444444444444444444444444444444444444");
//            CategoryInfo categoryInfo = productInfo1.getCategory();
//            System.out.println("5555555555555555555555555555555555555555555");
//            List<Offer> offerPercent = categoryInfo.getOffer();
//            System.out.println("66666666666666666666666666666666666666666666"+offerPercent);
//            for (Offer offer : offerPercent) {
//                System.out.println("7777777777777777777777777777777777777777777"+offer);
//                int offPercent = offer.getCategoryOffPercentage();
//                System.out.println("8888888888888888888888888888888888888888888888");
//                if (value == offPercent) {
//                    System.out.println("9999999999999999999999999999999999999999999999");
//                    System.out.println(".......value  =" + value + " .......off = " + offer);
//                    System.out.println("100000000000000000000000000000000000000");
//                    model.addAttribute("pageSize", pageSize);
//                    model.addAttribute("productInfo", productInfo);
//                    model.addAttribute("category", category);
//                    model.addAttribute("brands", brands);
//
//                } else {
//                    System.out.println("bad data>.....no........................");
//                }
//            }
//        }
//        return  null;
//
//    }



    /*END FILTER #30% OFF  PRODUCT*/



    /*END OFFER FILTERING*/

    /*SINGLE PRODUCT /*SINGLE PRODUCTS SHOW*/
    @GetMapping("/singleProduct/{uuid}")
    public String showSingleProduct(@PathVariable UUID uuid, Model model) {
        Optional<ProductInfo> productInfo = productService.getProduct(uuid);
        ProductInfo product = productInfo.get();
        Long currentStock = product.getStock();
        System.out.println("single product ");
        if (product != null) {
            System.out.println("single product if");
            List<Offer> productOffer = product.getOffer();
            System.out.println("product offer percent" + productOffer);

            List<CategoryInfo> category = categoryService.findAll();

            CategoryInfo categories = product.getCategory();
            List<Offer> categoryOffers = offerService.getCategoryOffers(categories);

            List<Offer> productOff = offerService.getProductOffers(product);

            System.out.println("product offer > list>>                            " + productOff);

            System.out.println("catgory>>offer percent                           +" + categoryOffers);


            /*NEW*/

            float discountPrice = 0f;

            for (Offer productWiseOffer : productOff) {
                for (Offer categoryOffer : categoryOffers) {
                    if (productWiseOffer.getCategoryOffPercentage() > categoryOffer.getCategoryOffPercentage()) {
                        // Product offer percentage is greater than category offer percentage
                        // Handle this case
                        discountPrice = product.getPrice() - (product.getPrice() * productWiseOffer.getCategoryOffPercentage() / 100);
                        System.out.println(" products discount Amount  is = " + discountPrice);
                        product.setDiscountedPrice(discountPrice);
                        System.out.println("product of is grater  " + productWiseOffer.getCategoryOffPercentage());

                        System.out.println("product  offer applied>>" + discountPrice);

                        model.addAttribute("productInfo", productInfo);
                        return "shop/singleproduct";
                    } else {

                        discountPrice = product.getPrice() - (product.getPrice() * categoryOffer.getCategoryOffPercentage() / 100);
                        System.out.println(" category discount Amount  is = " + discountPrice);
                        product.setDiscountedPrice(discountPrice);
                        System.out.println("category of is grater  " + categoryOffer.getCategoryOffPercentage());
                        System.out.println("category offer applied>>" + discountPrice);
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




            /*ENDD*/




//            if (productOffer .isEmpty() && categoryOffers .isEmpty()) {
//                int categoryOfferPercent=categoryOffers.get(0).getCategoryOffPercentage();
//                int productOfferPercent=productOffer.get(0).getCategoryOffPercentage();
//                System.out.println("offer in single category  ......>>>>>>>"+categoryOfferPercent);
//                System.out.println(" Offer single product product ....>>>>>"+productOfferPercent);
//
//                if(categoryOfferPercent>productOfferPercent){
//                   float discountPrice=product.getPrice()-  (product.getPrice()*categoryOfferPercent/100);
//                   product.setDiscountedPrice(discountPrice);
//
//                   System.out.println("category offer applied>>"+discountPrice);
//                  model.addAttribute("productInfo", productInfo);
//                  return "shop/singleproduct";}
////                else if (categoryOfferPercent<productOfferPercent)
//                else
//                {
//                    float discountPrice=product.getPrice()- (product.getPrice()*productOfferPercent/100);
//                  product.setDiscountedPrice(discountPrice);
//                  System.out.println("product offer applied..."+discountPrice);
//                  model.addAttribute("productInfo", productInfo);
//                  return "shop/singleproduct";
//                }
//            }else {
//
//            System.out.println("product have no offer>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
//            model.addAttribute("productInfo", productInfo);
//            return "shop/singleproduct";
//                }

//        }else{
//            float discount=1;
//            product.setDiscountedPrice(discount);
//            model.addAttribute("error", "Product not found");
//            return "shop/singleproduct";
//        }
//    }

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
        System.out.println("...home...categoryInfo"+categoryInfo);


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
        System.out.println("about;;;;;;;;;;;;;;");
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
        System.out.println("referral......"+referral);
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
            System.out.println("Referral code ....." +referralCode);
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



//        System.out.println("email id  ......."+email);
//                           Optional<UserEntity> user =userService.getUserdata(username);
//                           UserEntity userEntity=user.get();
//                     String referralCode  =  userEntity.getNewUserReferral();
//        System.out.println("Referral code ....." +referralCode);
//        String subject="Referral Message ";
//        String body="Your referral message for register Al-start website,Congrats ";
//
//       shopService .sendMail( email, subject, referralCode);
//
//            System.out.println("Mail send successfully.......");
//
//           return "redirect:/shop/referral-code";
//    }


    @GetMapping("/showWalletMoney")
    public String getMoney(Model model,@AuthenticationPrincipal(expression = "username")String  username){


        shopService.getMoney(username);
       return "shop/wallet";

    }


//    @GetMapping("/createWallet")
//    public String createWallet(@AuthenticationPrincipal(expression = "username")String  username,Model model){
//        System.out.println(username);
//        UserEntity userEntity = userService.findByUsernames(username);
//        Wallet newUseWallet = new Wallet();
//        newUseWallet.setEarnedMoney(1f);
//        newUseWallet.setUserEntity(userEntity);
////        userEntity.setWallet(newUseWallet);
//        shopService.saveWallet(newUseWallet);
//        System.out.println("userEntity....."+userEntity);
//        System.out.println(newUseWallet);
//        return "shop/wallet";
//    }


    /*end referral */


//        if (priceRange != null) {
//            String[] rangeParts = priceRange.split("-");
//            double minPrice = Double.parseDouble(rangeParts[0]);
//            double maxPrice = Double.parseDouble(rangeParts[1]);
//            System.out.println("minprice  =="+minPrice);
//            System.out.println("maxPrice  =="+maxPrice);
//            /*find all product*/
//            Page<ProductInfo> products = productService.loadAllProducts(PageRequest.of(pageable.getPageNumber(), pageSize));
//            List<ProductInfo> filteredProducts = new ArrayList<>();
//
//            for (ProductInfo productData : products) {
//                if (minPrice <= productData.getPrice() && productData.getPrice() <= maxPrice) {
//
//
//                    System.out.println("product price get products"+productData.getPrice());
//                    System.out.println("productInfo>>>>>>>>>>>[]   "+productData);
//                    filteredProducts.add(productData);
//                }
//            }
//            Page<ProductInfo> productInfo = new PageImpl<>(filteredProducts, pageable, filteredProducts.size());
//
//            System.out.println("productInfo>>>>>>>>>>>[] array list   "+productInfo);
//            model.addAttribute("pageSize",pageSize);
//            model.addAttribute("productInfo",productInfo);
//            model.addAttribute("category",category);
//            model.addAttribute("brands",brands);
//            return "shop/filterProduct";
//        } else {
//            Page<ProductInfo> productInfo = productService.loadAllProducts(PageRequest.of(pageable.getPageNumber(), pageSize));
//            model.addAttribute("pageSize",pageSize);
//            model.addAttribute("productInfo",productInfo);
//            model.addAttribute("category",category);
//            model.addAttribute("brands",brands);
//            ; // No price range selected, show all products
//        }
//        return "shop/filterProduct";
//    }

    /*END*/



//    @GetMapping(value = "/search", params = "keyword")
//    public String shopSearch(Model model, @Param("keyword") String keyword) {
//        System.out.println("search>begin.....>>>>>>>");
//        try {
//            List<ProductInfo> productInfo;
//
//            if (keyword != null && !keyword.isEmpty()) {
//                productInfo = shopService.searchProductName(keyword);
//                System.out.println("search>>>>if" + productInfo);
//            } else {
//                System.out.println("search--else------->>>");
//                productInfo = shopService.loadAllProduct();
//            }
//            System.out.println("productInfo>>>>>>>>>>>>>search "+productInfo);
//
//            List<CategoryInfo> category = categoryService.findAll();
//            List<Brand>brands=brandService.loadAllBrand();
//            model.addAttribute("category",category);
//            model.addAttribute("brands",brands);
//            model.addAttribute("productInfo", productInfo);
//
//            return "shop/filterProduct";
//        } catch (Exception e) {
//            e.printStackTrace();
//            model.addAttribute("errorMessage", "An error occurred while processing your request.");
//            return "error";
//        }
//    }


//    @GetMapping("/filterByCategory")
//    public String filterByCategory(@RequestParam("selectedCategoryUuid") UUID selectedCategoryUuid,
//                                   @RequestParam(name = "pageSize", defaultValue = "6") int pageSize,Model model,
//                                   @PageableDefault(size = 6) Pageable pageable) {
//        System.out.println("Selected Category UUID: " + selectedCategoryUuid);
//
//        // Use the selectedCategoryUuid to filter products or perform other actions
////        List<ProductInfo> productInfo = productService.getCategoryByProduct(selectedCategoryUuid);
////        Page<ProductInfo> productInfo = productService.loadAllProducts(PageRequest.of(pageable.getPageNumber(), pageSize));
//        Page<ProductInfo> productInfo = productService.getCategoryProducts(selectedCategoryUuid, PageRequest.of(pageable.getPageNumber(), pageSize));
//        List<CategoryInfo> category = categoryService.findAll();
//        List<Brand> brands = brandService.loadAllBrand();
//        System.out.println("productI<<<<<nfo"+productInfo);
//
//        model.addAttribute("pageSize", pageSize);
//        model.addAttribute("productInfo", productInfo);
//        model.addAttribute("category", category);
//        model.addAttribute("brands", brands);
//
//        return "shop/filterProduct";
//    }


    /*Show all products in filterProducts html*/

//    @GetMapping("/showAllProductsList")
//    public  String showAllProducts(Model model){
//        List<ProductInfo>productInfo=productService.loadAllProduct();
//        List<CategoryInfo> category = categoryService.findAll();
//        List<Brand>brands=brandService.loadAllBrand();
//
//        model.addAttribute("category",category);
//        model.addAttribute("productInfo",productInfo);
//        model.addAttribute("brands",brands);
//        return "shop/filterProduct";
//    }


    //    @GetMapping // This handles the "/shop" URL without "/view"
//    public String redirectToShopView() {
//        return "redirect:/shop/view"; // Redirect to the shop view
//    }




}