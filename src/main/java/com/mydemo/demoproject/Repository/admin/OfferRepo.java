package com.mydemo.demoproject.Repository.admin;

import com.mydemo.demoproject.Entity.CategoryInfo;
import com.mydemo.demoproject.Entity.Offer;
import com.mydemo.demoproject.Entity.ProductInfo;
import com.mydemo.demoproject.service.admin.offer.OfferService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OfferRepo extends JpaRepository<Offer,UUID> {

    boolean existsByProductInfo_Name(String productName);
    boolean existsByCategoryInfo_Categoryname(String categoryName);

    List<Offer> findByCategoryInfo(CategoryInfo category);

    List<Offer>findByProductInfo(ProductInfo product);




}
