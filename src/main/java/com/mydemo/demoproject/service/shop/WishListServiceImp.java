package com.mydemo.demoproject.service.shop;

import com.mydemo.demoproject.Entity.ProductInfo;
import com.mydemo.demoproject.Entity.WishList;
import com.mydemo.demoproject.Repository.shop.WishListRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class WishListServiceImp implements WishListService {

    @Autowired
    WishListRepo wishListRepo;

    @Override
    public void deleteProduct(UUID uuid) {
        wishListRepo.deleteById(uuid);
    }

    @Override
    public void moveToWishList(WishList wishlist) {
        wishListRepo.save(wishlist);
    }

    @Override
    public List<WishList> getWishListItems() {
        List<WishList> wishList = wishListRepo.findAll();
        return wishList;
    }

    @Override
    public Optional<ProductInfo> getProduct(UUID uuid) {
        Optional<WishList> wishlistItem = wishListRepo.findById(uuid);

        // Check if the wishlist item with the given UUID exists
        if (wishlistItem.isPresent()) {
            ProductInfo productInfo = wishlistItem.get().getProductInfo();
            return Optional.ofNullable(productInfo);
        } else {
            return Optional.empty();
        }
    }
}