package com.mydemo.demoproject.service.admin.offer;

import com.mydemo.demoproject.Entity.Coupon;
import com.mydemo.demoproject.Entity.Offer;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.UUID;

public interface OfferService {

    Page<Offer> findPaginated(int pageNo, int pageSize);

   void saveOffer(Offer offer);

    Optional<Offer> getCouponBYId(UUID uuid);
    public void unblockOffer(UUID uuid);

    public void blockOffer(UUID uuid);

}
