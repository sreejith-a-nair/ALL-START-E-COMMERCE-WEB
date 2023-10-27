package com.mydemo.demoproject.controller.category;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mydemo.demoproject.Entity.CategoryInfo;

import com.mydemo.demoproject.Entity.ProductInfo;
import com.mydemo.demoproject.service.admin.cartegory.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Controller
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /*pagination in category*/

    @GetMapping("/home")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String showAllCategory(Model model){
        return   findPaginated(1,model);
    }


    /*pagination*/

    @GetMapping("/page/{pageNo}")
    public String findPaginated(@PathVariable(value = "pageNo") int pageNo,Model model){
        int pageSize=5;
        Page<CategoryInfo> page=categoryService.findPaginated(pageNo,pageSize);
        List<CategoryInfo>categoryInfo=page.getContent();


        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("categoryInfo", categoryInfo);

        return "admin/category";
    }

    /*pagination end*/


    /* 2 add Category*/

    @GetMapping("/addcategory")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String addCategory(@RequestParam(value="error",required=false)String error,Model model )throws JsonProcessingException
    {
        if (error != null && error.equals("categoryExists")) {
            model.addAttribute("error", "Category already exists.");
        }
        return "admin/add-category";
    }

    /*3 save / update*/
    @PostMapping("/save")
    public String save(@ModelAttribute CategoryInfo categoryInfo,Model model) {


        try {
            categoryService.save(categoryInfo);
            return "redirect:/category/home";
        } catch (Exception ex) {
            return "redirect:/category/addcategory?error=categoryExists";
        }


    }


    @GetMapping("/edit/{uuid}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String editCategory(@PathVariable UUID uuid, Model model) {
        Optional<CategoryInfo> categoryopptional = categoryService.getCategory(uuid);
        if (categoryopptional.isPresent()) {
            CategoryInfo categoryInfo = categoryopptional.get();
            model.addAttribute("categoryInfo",categoryInfo);
            return "admin/edit-category";
        } else {
            return String.valueOf(model.addAttribute("error message", "Invalid valid data"));
        }
    }

    /*category enable or disable*/

    @GetMapping("/enable/{uuid}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String enableCategory(@PathVariable UUID uuid)
    {
        categoryService.enableCategory(uuid);
        return "redirect:/category/home";
    }

    @GetMapping("/disable/{uuid}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String disableCategory(@PathVariable UUID uuid)
    {
        categoryService.disableCategory(uuid);
        return  "redirect:/category/home";
    }

    /*NEW SEARCH*/
@GetMapping("/search")
public String categorySearch(@RequestParam("keyword") String keyword,
                                         @PageableDefault(size = 2)Pageable pageable                                                                                    ,
                                         Model model) {

Page<CategoryInfo>categoryInfo=categoryService.searchCategory(keyword,pageable);
model.addAttribute("categoryInfo",categoryInfo);
  return "admin/category";
}
/*END*/


    /*logout*/
    @GetMapping("/logout")
    public String handleLogoutRequest(HttpServletRequest request) {

        request.getSession().invalidate();
        return "redirect:/login?logout";
    }



}

