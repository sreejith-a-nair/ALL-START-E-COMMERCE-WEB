package com.mydemo.demoproject.service.shop;

import com.mydemo.demoproject.Entity.Cart;
import com.mydemo.demoproject.Entity.CategoryInfo;
import com.mydemo.demoproject.Entity.ProductInfo;
import com.mydemo.demoproject.Entity.UserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartService {
    Optional<CategoryInfo> getCategoryDetails(UUID uuid);
  /*get*/
    List<Cart> getCartItems();

    /*Get cart by id*/
    Optional<Cart> getCartById(UUID uuid);

  /*delete*/
     void deleteProduct(UUID uuid);
  /*add*/
    void saveToCart (Cart cart);

    List<Cart> findByUserEntity_Usernames (String username);


      Cart findByProductInfo_Uuid(UUID productUuid);


      /*new update*/
//      boolean productExistsInCart(UserEntity userEntity, ProductInfo productInfo);

    /*remove from cart*/
    void removeFromCart(String username, UUID productId);


    Double findTotal(List<Cart> cart);

  public Optional<ProductInfo> getProduct(UUID uuid);



/*single product authetication*/
 Cart findCartItem(UserEntity userEntity, ProductInfo productInfo);



}
