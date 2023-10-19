package com.mydemo.demoproject.service.admin.offer;

import com.mydemo.demoproject.Entity.CategoryInfo;
import com.mydemo.demoproject.Entity.Offer;
import com.mydemo.demoproject.Entity.ProductInfo;
import com.mydemo.demoproject.Repository.admin.CategoryRepo;
import com.mydemo.demoproject.Repository.admin.OfferRepo;
import com.mydemo.demoproject.Repository.admin.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OfferServiceImp implements  OfferService{
    @Autowired
    CategoryRepo categoryRepo;

    @Autowired
    OfferRepo offerRepo;

    @Autowired
    ProductRepo productRepo;
//    @Override
//    public void saveOffer(Offer offer) {
//        offer.set(true);
//        offerRepo.save(offer);
//    }

    @Override
    public Page<Offer> findPaginated(int pageNo, int pageSize) {
        Pageable pageable= PageRequest.of(pageNo-1,pageSize);
        return this.offerRepo.findAll(pageable);
    }

    @Override
    public void saveOffer(Offer offer) {
        offer.setEnabled(true);
        offerRepo.save(offer);
    }

    @Override
    public Optional<Offer> getCouponBYId(UUID uuid) {
        return offerRepo.findById(uuid);
    }

    @Override
    public void unblockOffer(UUID uuid) {
        Optional<Offer> offer =offerRepo.findById(uuid);
        if (offer.isPresent()) {
            System.out.println("unblocked offer>>>>>>>>>>>>");
            Offer offerData = offer.get();
            offerData.setEnabled(true);
            offerRepo.save(offerData);
        } else {
            throw new EntityNotFoundException("offer not found with ID: " + uuid);
        }
    }

    @Override
    public void blockOffer(UUID uuid) {
        Optional<Offer> offer =offerRepo.findById(uuid);
        if (offer.isPresent()) {
            System.out.println("blocked offer>>>>>>>>>>>>");
            Offer offerData = offer.get();
            offerData.setEnabled(false);
            offerRepo.save(offerData);
        } else {
            throw new EntityNotFoundException("offer not found with ID: " + uuid);
        }
    }

    @Override
    public Offer addOfferCategory(Offer offer, UUID categoryId) throws ChangeSetPersister.NotFoundException {

        CategoryInfo category = categoryRepo.findById(categoryId)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);
        offer.setCategoryInfo(category);
        return offerRepo.save(offer);
    }

    @Override
    public Offer addOfferProduct(Offer offer, UUID productId) throws ChangeSetPersister.NotFoundException {
        ProductInfo product = productRepo.findById(productId)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);
        offer.setProductInfo(product);
        return offerRepo.save(offer);
    }


    public boolean offerExistsForProductByName(String productName) {
        return offerRepo.existsByProductInfo_Name(productName);
    }

    public boolean offerExistsForCategoryByName(String categoryName) {
        return offerRepo.existsByCategoryInfo_Categoryname(categoryName);
    }

    @Override
    public List<Offer> getAllOffer() {
        return offerRepo.findAll();
    }

    @Override
    public List<Offer> getCategoryOffers(CategoryInfo category) {
        return offerRepo.findByCategoryInfo(category);
    }

    @Override
    public List<Offer> getProductOffers(ProductInfo productOff) {
        return offerRepo.findByProductInfo(productOff);
    }





}
