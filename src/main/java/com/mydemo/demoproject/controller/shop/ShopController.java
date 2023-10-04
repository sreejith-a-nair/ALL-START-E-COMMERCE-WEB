package com.mydemo.demoproject.controller.shop;

import com.mydemo.demoproject.Entity.*;
import com.mydemo.demoproject.service.admin.brand.BrandService;
import com.mydemo.demoproject.service.admin.cartegory.CategoryService;
import com.mydemo.demoproject.service.admin.product.ImageService;
import com.mydemo.demoproject.service.admin.product.ProductService;
import com.mydemo.demoproject.service.shop.ShopService;
import com.mydemo.demoproject.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    /*ShopView*/
    @GetMapping("/view")
    public String shopView() {
        return "shop/shopView";

    }


    /*PRODUCT SEARCH*/

    @GetMapping(value = "/search", params = "keyword")
    public String productSearch(@RequestParam @Param("keyword") String keyword, Model model,
                                @RequestParam(name = "pageSize", defaultValue = "6") int pageSize,
                                @PageableDefault(size = 6) Pageable pageable){
        Page<ProductInfo> productInfo = productService.searchProduct(keyword, pageable);
        List<Brand>brands=brandService.loadAllBrand();
        List<CategoryInfo> category = categoryService.findAll();
//        List<ProductInfo> lowProducts =productService.checkStock();
        // Sort the lowProducts list by stock in ascending order
//        Collections.sort(lowProducts, Comparator.comparing(Product::getStock));
//        model.addAttribute("lowProducts", lowProducts);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("brands",brands);
        model.addAttribute("category",category);
        model.addAttribute("productInfo",productInfo);
        return "shop/filterProduct";
}
/*SEARCH END*/



    /*SHOW ALL PRODUCT LIST*/
    @GetMapping("/showAllProductsList")
    public String getProduct(@RequestParam(name = "pageSize", defaultValue = "9") int pageSize,
                             @PageableDefault(size = 9) Pageable pageable, Model model) {
        Page<ProductInfo> productInfo = productService.loadAllProducts(PageRequest.of(pageable.getPageNumber(), pageSize));
        List<Brand>brands=brandService.loadAllBrand();
        List<CategoryInfo> category = categoryService.findAll();
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("productInfo", productInfo);
        model.addAttribute("category",category);
        model.addAttribute("brands",brands);
        return "shop/filterProduct";
}
/*SHOW ALL PRODUCT LIST END /*END SEARCH PAGINATION*/


    /*FILTER CATEGORY BY PRODUCT*/
    @GetMapping("/filterByCategory")
    public String filterByCategory(@RequestParam("selectedCategoryUuid") UUID selectedCategoryUuid,
                                   @RequestParam(name = "pageSize", defaultValue = "6") int pageSize, Model model,
                                   @PageableDefault(size = 6) Pageable pageable) {

        Page<ProductInfo> productInfo = productService.getCategoryProducts(selectedCategoryUuid, pageable);
        System.out.println("Category products :::::::"+productInfo);
        List<CategoryInfo> category = categoryService.findAll();
        List<Brand> brands = brandService.loadAllBrand();

        model.addAttribute("pageSize", pageSize);
        model.addAttribute("productInfo", productInfo);
        model.addAttribute("category", category);
        model.addAttribute("brands", brands);


        return "shop/filterProduct";
    }

/*END FILTER BY CATEGORY*/   /*END*/





/*3 CATEGORY EXPLORE */
@GetMapping("/categoryExplore/{uuid}")
public String filterCategory(@PathVariable UUID uuid,Model model,
                             @RequestParam(name = "pageSize", defaultValue = "6") int pageSize,
                             @PageableDefault(size = 6) Pageable pageable) {

    Page<ProductInfo> productInfo = productService.getCategoryProducts(uuid, pageable);
    List<CategoryInfo> category = categoryService.findAll();
    List<Brand>brands=brandService.loadAllBrand();

    model.addAttribute("pageSize", pageSize);
    model.addAttribute("productInfo",productInfo);
    model.addAttribute("category",category);
    model.addAttribute("brands",brands);
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

        System.out.println("PRICE RANGE PRODUCTS ONLY :    "+priceRange);
        if (priceRange != null) {
            String[] rangeParts = priceRange.split("-");
            float minPrice = Float.parseFloat(rangeParts[0]);
            float maxPrice = Float.parseFloat(rangeParts[1]);

            // Load all products matching the price range
            Page<ProductInfo> productInfo = productService.getProductsInPriceRange(minPrice, maxPrice, PageRequest.of(page, pageSize));

            System.out.println("PRICE RANGE PRODUCTS ONLY :    "+productInfo);

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


    /*SINGLE PRODUCT /*SINGLE PRODUCTS SHOW*/
    @GetMapping("/singleProduct/{uuid}")
    public String showSingleProduct(@PathVariable UUID uuid, Model model) {
        Optional<ProductInfo> productInfo = productService.getProduct(uuid);
        ProductInfo product=productInfo.get();
        Long currentStock=product.getStock();
        if(product!=null) {
            model.addAttribute("productInfo", productInfo);
            return "shop/singleproduct";
        }else{
            model.addAttribute("error", "Product not found");
            return "shop/singleproduct";
        }
    }

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