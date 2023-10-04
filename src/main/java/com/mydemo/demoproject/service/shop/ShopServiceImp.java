package com.mydemo.demoproject.service.shop;

import com.mydemo.demoproject.Entity.ProductInfo;
import com.mydemo.demoproject.Repository.admin.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ShopServiceImp implements  ShopService{

    @Autowired
    ProductRepo productRepo;
//    @Override
//    public List<ProductInfo> searchProductName(String keyword) {
//        return productRepo.findByProductNameKeyword(keyword);
//    }

    @Override
    public List<ProductInfo> loadAllProduct() {
        List  <ProductInfo> productList;
        productList = productRepo.findAll();
        return productList;
    }

    /* search product*/



}
