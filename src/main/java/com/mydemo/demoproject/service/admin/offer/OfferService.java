package com.mydemo.demoproject.service.admin.offer;

import com.mydemo.demoproject.Entity.CategoryInfo;
import com.mydemo.demoproject.Entity.Offer;
import com.mydemo.demoproject.Entity.ProductInfo;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OfferService {

    Page<Offer> findPaginated(int pageNo, int pageSize);

   void saveOffer(Offer offer);

    Optional<Offer> getCouponBYId(UUID uuid);
    public void unblockOffer(UUID uuid);

    public void blockOffer(UUID uuid);

     Offer addOfferCategory(Offer offer, UUID categoryId) throws ChangeSetPersister.NotFoundException;
    Offer addOfferProduct(Offer offer, UUID productId) throws ChangeSetPersister.NotFoundException;

     boolean offerExistsForProductByName(String productName);

     boolean offerExistsForCategoryByName(String categoryName);

   List<Offer> getAllOffer();


   /*new updates*/
    List<Offer> getCategoryOffers(CategoryInfo category);

    List<Offer>getProductOffers( ProductInfo  productOff);



}
