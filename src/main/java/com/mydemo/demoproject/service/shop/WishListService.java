package com.mydemo.demoproject.service.shop;

import com.mydemo.demoproject.Entity.ProductInfo;
import com.mydemo.demoproject.Entity.WishList;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface WishListService {
    /*delete*/
    public void deleteProduct(UUID uuid);
     /*add*/
    void moveToWishList(WishList wishlist);
    /*get*/
    List<WishList> getWishListItems();

    Optional<ProductInfo> getProduct(UUID uuid);

}
