package com.mydemo.demoproject.service.admin.offer;

import com.mydemo.demoproject.Entity.Coupon;
import com.mydemo.demoproject.Entity.Offer;
import com.mydemo.demoproject.Repository.admin.OfferRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;
import java.util.UUID;

@Service
public class OfferServiceImp implements  OfferService{


    @Autowired
    OfferRepo offerRepo;
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

}
