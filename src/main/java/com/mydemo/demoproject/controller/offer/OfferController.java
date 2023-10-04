package com.mydemo.demoproject.controller.offer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mydemo.demoproject.Entity.Coupon;
import com.mydemo.demoproject.Entity.Offer;
import com.mydemo.demoproject.service.admin.offer.OfferService;
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
@RequestMapping("/offer")
public class OfferController {


    @Autowired
    OfferService offerService;



    @GetMapping("/home")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String showAllOffer(Model model){
        return   findPaginated(1,model);
    }

    /*pagination*/

    @GetMapping("/page/{pageNo}")
    public String findPaginated(@PathVariable(value = "pageNo") int pageNo, Model model){
        int pageSize=5;
        Page<Offer> page=offerService.findPaginated(pageNo,pageSize);
        List<Offer> offerList=page.getContent();
        System.out.println("offer in pagination(("+offerList);
        System.out.println("page in pagination(("+page);

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("offerList", offerList);

        return "admin/offer";
    }




    @GetMapping("/addOffer")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String addOffer(@RequestParam(value="error",required=false)String error, Model model)throws JsonProcessingException {

        if (error != null && error.equals("offerExists")) {
            model.addAttribute("error", "Offer already exists.");
        }

        return "admin/add-offer";
    }


    @PostMapping("/saveOffer")
    public  String saveOffer(@ModelAttribute("offer")Offer offer, Model model){

        /*find duplicate coupon add or not*/

        System.out.println("offer>>>>>>>"+offer);
        try {
            offerService.saveOffer(offer);
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


}
