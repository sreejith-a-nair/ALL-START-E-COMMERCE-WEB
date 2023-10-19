package com.mydemo.demoproject.controller.offer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mydemo.demoproject.Entity.CategoryInfo;
import com.mydemo.demoproject.Entity.Coupon;
import com.mydemo.demoproject.Entity.Offer;
import com.mydemo.demoproject.Entity.ProductInfo;
import com.mydemo.demoproject.service.admin.cartegory.CategoryService;
import com.mydemo.demoproject.service.admin.offer.OfferService;
import com.mydemo.demoproject.service.admin.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/offer")
public class OfferController {


    @Autowired
    OfferService offerService;

    @Autowired
    ProductService productService;

    @Autowired
    CategoryService categoryService;


    @GetMapping("/home")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String showAllOffer(Model model) {
        return findPaginated(1, model);
    }

    /*pagination*/

    @GetMapping("/page/{pageNo}")
    public String findPaginated(@PathVariable(value = "pageNo") int pageNo, Model model) {
        int pageSize = 5;
        Page<Offer> page = offerService.findPaginated(pageNo, pageSize);
        List<Offer> offerList = page.getContent();
        System.out.println("offer in pagination((" + offerList);
        System.out.println("page in pagination((" + page);

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("offerList", offerList);

        return "admin/offer";
    }


    @GetMapping("/addOffer")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String addOffer(@RequestParam(value = "error", required = false) String error, Model model) throws JsonProcessingException {


        List<ProductInfo> productInfo = productService.loadAllProduct();

        List<CategoryInfo> categoryInfo = categoryService.findAllCategory();
        System.out.println("productInfo" + productInfo);
        System.out.println("productInfo" + productInfo);
        if (error != null && error.equals("offerExists")) {
            model.addAttribute("error", "Offer already exists.");
        }

        model.addAttribute("productInfo", productInfo);
        model.addAttribute("categoryInfo", categoryInfo);
        return "admin/add-offer";
    }


    @PostMapping("/saveOffer")
    public String saveOffer(@RequestParam("CategoryOffPercentage") Integer CategoryOffPercentage,
                            @RequestParam(value = "categoryUuid", required = false) UUID categoryInfo,
                            @RequestParam(value = "productUuid", required = false) UUID productInfo,
                            @RequestParam("count") Integer count,
                            @RequestParam("expiryDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate expiryDate,
                            Model model) {


        if (categoryInfo != null && productInfo != null) {
            System.out.println("ifffffffffffffffffffffffffffffffffff  f&&& ffffffffffffffffffffffffff................................");
            return "redirect:/offer/addOffer?error=chooseOne";
        }
        if (productInfo != null) {
            Optional<ProductInfo> product = productService.getProduct(productInfo);
            String productName = product.get().getName();
            System.out.println("iffffffffffffffffffffffffffffffffff22222222222ffffffffffffffffffffffffffff................................");
            if (offerService.offerExistsForProductByName(productName)) {
                return "redirect:/offer/addOffer?error=offerExists";
            }
        } else if (categoryInfo != null) {
            Optional<CategoryInfo> category = categoryService.getCategoryById(categoryInfo);
            String categoryName = category.get().getCategoryname();
            System.out.println("iffffffffffffffffffffffffff  else if   fffffffffffffffffffffffffffffffffffff................................");
            if (offerService.offerExistsForCategoryByName(categoryName)) {
                System.out.println("iffffffffffffffffffffffffffffffffff if    fffffffffffffffffffff................................");
                return "redirect:/offer/addOffer?error=offerExists";
            }
        }
            /*end find duplicate coupon add or not*/
            try {

                Offer offer = new Offer();
                offer.setCategoryOffPercentage(CategoryOffPercentage);
                offer.setEnabled(true);
                offer.setExpiryDate(expiryDate);
                offer.setCount(count);
                System.out.println("get-it>>>");

                if (categoryInfo != null) {
                    offer = offerService.addOfferCategory(offer, categoryInfo);
                } else {
                    offer = offerService.addOfferProduct(offer, productInfo);
                }

                return "redirect:/offer/home";
            } catch (Exception ex) {
                return "redirect:/offer/addOffer?error=offerExists";
            }
        }

    /*update coupon*/

    @GetMapping("/edit/{uuid}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String editOffer(@PathVariable UUID uuid, Model model) {
//        System.out.println("edit product method.>>>>>>>>>>>>>>.............." + uuid);
        Optional<Offer> offerById = offerService.getCouponBYId(uuid);
        if (offerById.isPresent()) {
//            System.out.println("is present..............................");
            Offer offerInfo = offerById.get();
            model.addAttribute("offer",offerInfo);
            return "admin/edit-offer";
        } else {
            return String.valueOf(model.addAttribute("error message", "Invalid valid data"));
        }
    }


    @PostMapping("/updateOffer")
    public String updateOffer(@ModelAttribute("offer") Offer updatedOffer) {

        Optional<Offer> existingOffer = offerService.getCouponBYId(updatedOffer.getUuid());
        if (existingOffer.isPresent()) {
            Offer offerToUpdate = existingOffer.get();

            offerToUpdate.setCategoryOffPercentage(updatedOffer.getCategoryOffPercentage());
            offerToUpdate.setExpiryDate(updatedOffer.getExpiryDate());
            offerToUpdate.setCount(updatedOffer.getCount());
            offerToUpdate.setEnabled(true);
            offerService.saveOffer(offerToUpdate);

            return "redirect:/offer/home";
        } else {
            return "redirect:/offer/home?error=OfferNotFound";
        }
    }
    /*end*/

    /*Block offer*/
    @GetMapping("/block/{uuid}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String blockOffer(@PathVariable UUID uuid)
    {
       offerService.blockOffer(uuid);
        return "redirect:/offer/home";
    }


    /*Un-block offer*/
    @GetMapping("/unblock/{uuid}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String unBlockOffer(@PathVariable UUID uuid)
    {
        offerService.unblockOffer(uuid);
        return  "redirect:/offer/home";
    }

    /*show offer*/
    @GetMapping("/show-offer")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String showOffer(Model model) {

        List<Offer> offerList = offerService.getAllOffer();


        List<ProductInfo> productInfoList = new ArrayList<>();
        List<CategoryInfo> categoryInfoList = new ArrayList<>();

        for (Offer offer : offerList) {
            int offerPercent = offer.getCategoryOffPercentage();
            CategoryInfo categoryInfo = offer.getCategoryInfo();
            ProductInfo productInfo = offer.getProductInfo();

            if (categoryInfo != null) {
                List<ProductInfo> products = categoryInfo.getProducts();
                for (ProductInfo product : products) {
                    float productPrice = product.getPrice();
                    float categoryDiscountedPrice = productPrice - (productPrice * offerPercent / 100);
                    product.setPrice(productPrice);
                    productInfoList.add(product);
                }
               categoryInfoList.add(categoryInfo);
            } else if (productInfo != null) {
                float productPrice = productInfo.getPrice();
                float productDiscountedPrice = productPrice - (productPrice * offerPercent / 100);
                productInfo.setPrice(productPrice);

            }
            productInfoList.add(productInfo);
        }
        model.addAttribute("categoryInfoList", categoryInfoList);
        model.addAttribute("productInfoList", productInfoList);

        return "shop/shopView"; // Replace with your actual template name
    }

}
