//package com.mydemo.demoproject.controller;
//
//import com.mydemo.demoproject.Entity.CategoryInfo;
//import com.mydemo.demoproject.Entity.ProductInfo;
//import com.mydemo.demoproject.service.admin.cartegory.CategoryService;
//import com.mydemo.demoproject.service.admin.product.ProductService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.web.PageableDefault;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//
//import java.awt.print.Pageable;
//import java.util.Optional;
//
//public class HomeController {
//
//
//    @Autowired
//    CategoryService categoryService;
//
//
//    @Autowired
//    ProductService productService;
//
//    @GetMapping("/")
//    public String View(Model model) {
//        Page<CategoryInfo> category = categoryService.getCategoryes();
//        model.addAttribute("category", category);
//        Page<ProductInfo> product = productService.getProducts();
//        model.addAttribute("product", product);
//        return "index";
//    }
//}