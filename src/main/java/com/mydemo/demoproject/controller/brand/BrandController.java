package com.mydemo.demoproject.controller.brand;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mydemo.demoproject.Entity.Brand;
import com.mydemo.demoproject.Entity.CategoryInfo;
import com.mydemo.demoproject.Entity.Coupon;
import com.mydemo.demoproject.service.admin.brand.BrandService;
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
@RequestMapping("/brand")
public class BrandController {
    @Autowired
    BrandService brandService;

//    /*old home*/
//    @GetMapping("/home")
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
//    public String showAllBrand(Model model) {
//        List<Brand> brands = brandService.findAll();
//        model.addAttribute("brands", brands);
//        return "admin/brand";
//    }

    /*pagination */

    @GetMapping("/home")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String showAllBrands(Model model){
        return   findPaginated(1,model);
    }


    /*pagination*/

    @GetMapping("/page/{pageNo}")
    public String findPaginated(@PathVariable(value = "pageNo") int pageNo,Model model){
        int pageSize=5;
        Page<Brand> page=brandService.findPaginated(pageNo,pageSize);
        List<Brand>brands=page.getContent();
        System.out.println("Brands in pagination(("+brands);
        System.out.println("page in pagination(("+page);

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("brands", brands);

        return "admin/brand";
    }


    /* pagination end*/


    @GetMapping("/brandItems")
    public String showBrand(Model model) {
        System.out.println("hello brand======");
        List<Brand> brands = brandService.findAll();
        System.out.println("brands]]]]]]]]"+brands);
//        model.addAttribute("brands", brands);
        model.addAttribute("brands",brands);
        return "shop/base";
    }

    @GetMapping("/addBrand")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String addCategory(@RequestParam(value="error",required=false)String error, Model model )throws JsonProcessingException
    {
        if (error != null && error.equals("BrandExists")) {
            model.addAttribute("error", "Brand already exists.");
        }
        return "admin/add-brand";
    }


    @GetMapping("/block/{uuid}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String blockProduct(@PathVariable UUID uuid)
    {
        brandService.blockBrand(uuid);
        return "redirect:/brand/home";
    }


    /*Un-block Product*/
    @GetMapping("/unblock/{uuid}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String unBlockProduct(@PathVariable UUID uuid)
    {
        brandService.unblockBrand(uuid);
        return  "redirect:/brand/home";
    }


    /*3 save / update*/
    @PostMapping("/save")
    public String save(@ModelAttribute Brand brand, Model model) {
        System.out.println("Save//////////////////////");

//        String errorMessage= String.valueOf(categoryService.save(categoryInfo));
//
//        if(!errorMessage.equals("success"))
//        {
//            model.addAttribute("error",errorMessage);
//            return "admin/add-category";
//        }
//        System.out.println("not present////////////////////////");

//        categoryService.save(categoryInfo);
//       return "redirect:/category/home";

        try {
            brandService.save(brand);
            return "redirect:/brand/home";
        } catch (Exception ex) {
            return "redirect:/brand/addBrand?error=categoryExists";
        }


    }


    /*edit Brand*/

    @GetMapping("/edit/{uuid}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String editBrand(@PathVariable UUID uuid, Model model) {
//        System.out.println("editCategory method..............." + id);
        Optional<Brand> brandOptional = brandService.getBrandById(uuid);
        if (brandOptional.isPresent()) {
            System.out.println("is present");
            Brand brand = brandOptional.get();
            model.addAttribute("brand",brand);
            return "admin/edit-brand";
        } else {
            return String.valueOf(model.addAttribute("error message", "Invalid valid data"));
        }
    }

    /*/brand update*/
    @PostMapping("/update")
    public String updateBrand(@ModelAttribute("brand") Brand updateBrand) {

        Optional<Brand> brandData = brandService.getBrandById(updateBrand.getUuid());


                Brand updateBrandData = brandData.get();
                updateBrandData.setName(updateBrand.getName());
                brandService.save(updateBrandData);
                return "redirect:/brand/home";

    }

    /*update / edit end*/

    @GetMapping(value = "/search", params = "keyword")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String searchCategory(Model model, @RequestParam("keyword") String keyword) {
//        System.out.println("search>begin.....>>>>>>>");

        try {

            List<Brand> brands;

            if (keyword != null && !keyword.isEmpty()) {
                brands= brandService.searchBrand(keyword);

                System.out.println("search>>>>if" + brands);

            } else {
                System.out.println("search--else------->>>");

                brands = brandService.loadAllBrand();
            }

            model.addAttribute("brands", brands);

            return "admin/brand";

        } catch (Exception e) {

            e.printStackTrace();

            model.addAttribute("errorMessage", "An error occurred while processing your request.");
            return "error";
        }

    }



}
