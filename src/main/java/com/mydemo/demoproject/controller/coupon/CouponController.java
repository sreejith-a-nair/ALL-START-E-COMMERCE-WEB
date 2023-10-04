package com.mydemo.demoproject.controller.coupon;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mydemo.demoproject.Entity.Coupon;
import com.mydemo.demoproject.service.admin.coupon.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/coupon")
public class CouponController {

    @Autowired
    CouponService couponService;

   @GetMapping("/addCoupon")
   @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String addCoupon(@RequestParam(value="error",required=false)String error,Model model)throws JsonProcessingException {

       if (error != null && error.equals("couponExists")) {
           model.addAttribute("error", "Coupon already exists.");
       }

       return "admin/addCoupon";
   }

   @PostMapping("/saveCoupon")
    public  String saveCoupon(@ModelAttribute("coupon")Coupon coupon, Model model){

/*find duplicate coupon add or not*/
//       Optional<Coupon> existingCoupon = couponService.getCouponBYId(UUID.fromString(coupon.getCode()));
//       String newCouponCode=coupon.getCode();
//       List<Coupon> couponList=couponService.findAll();
//       for(Coupon exsistCode: couponList) {
//
//           if(exsistCode.getCode().equals(newCouponCode)){
//
//               model.addAttribute()
//           }
//
//       }

       System.out.println("coupon>>"+coupon);
       try {
           couponService.saveCoupon(coupon);
           return "redirect:/coupon/home";
       } catch (Exception ex) {
           return "redirect:/coupon/addCoupon?error=couponExists";
       }
   }

   /*update coupon*/

    @PostMapping("/updateCoupon")
    public String updateCoupon(@ModelAttribute("coupon") Coupon updatedCoupon) {

        Optional<Coupon> existingCoupon = couponService.getCouponBYId(updatedCoupon.getUuid());
        if (existingCoupon.isPresent()) {
            Coupon couponToUpdate = existingCoupon.get();
            couponToUpdate.setName(updatedCoupon.getName());
            couponToUpdate.setCode(updatedCoupon.getCode());
            couponToUpdate.setOffPercentage(updatedCoupon.getOffPercentage());
            couponToUpdate.setMaxOff(updatedCoupon.getMaxOff());
            couponToUpdate.setExpiryDate(updatedCoupon.getExpiryDate());
            couponToUpdate.setCount(updatedCoupon.getCount());

            couponService.saveCoupon(couponToUpdate);

            return "redirect:/coupon/home";
        } else {
            return "redirect:/coupon/home?error=CouponNotFound";
        }
    }
/*end*/


/*Coupon home old*/
//    @GetMapping("/home")
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
//    public String showAllCoupon(Model model) {
//        List<Coupon> couponList = couponService.findAll();
//        model.addAttribute("couponList", couponList);
//        return "admin/coupon";
//    }


   @GetMapping("/home")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String showAllCoupons(Model model){
        return   findPaginated(1,model);
    }


    /*pagination*/

    @GetMapping("/page/{pageNo}")
    public String findPaginated(@PathVariable(value = "pageNo") int pageNo,Model model){
        int pageSize=5;
        Page<Coupon> page=couponService.findPaginated(pageNo,pageSize);
        List<Coupon>couponList=page.getContent();
        System.out.println("Coupon in pagination(("+couponList);
        System.out.println("page in pagination(("+page);

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("couponList", couponList);

        return "admin/coupon";
    }


 /* pagination end*/


    /*Block Product*/
    @GetMapping("/block/{uuid}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String blockCoupon(@PathVariable UUID uuid)
    {
        couponService.blockCoupon(uuid);
        return "redirect:/coupon/home";
    }


    /*Un-block Product*/
    @GetMapping("/unblock/{uuid}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String unBlockCoupon(@PathVariable UUID uuid)
    {
       couponService.unblockCoupon(uuid);
        return  "redirect:/coupon/home";
    }

    @GetMapping("/edit/{uuid}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String editOffer(@PathVariable UUID uuid, Model model) {
//        System.out.println("edit product method.>>>>>>>>>>>>>>.............." + uuid);
        Optional<Coupon> couponById = couponService.getCouponBYId(uuid);
        if (couponById.isPresent()) {
//            System.out.println("is present..............................");
            Coupon couponInfo = couponById.get();
            model.addAttribute("coupon",couponInfo);
            return "admin/edit-coupon";
        } else {
            return String.valueOf(model.addAttribute("error message", "Invalid valid data"));
        }
    }

}
