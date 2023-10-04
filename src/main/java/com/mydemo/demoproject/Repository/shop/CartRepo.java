package com.mydemo.demoproject.Repository.shop;

import com.mydemo.demoproject.Entity.Cart;
import com.mydemo.demoproject.Entity.CategoryInfo;
import com.mydemo.demoproject.Entity.ProductInfo;
import com.mydemo.demoproject.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartRepo extends JpaRepository<Cart, UUID> {

    List<Cart> findByUserEntity_Username(String username);

    List<Cart> findByUserEntity_UsernameAndProductInfo_Uuid(String username, UUID productId);

    Cart findByProductInfo_Uuid(UUID productUuid);

//    boolean productExistsInCart(UserEntity userEntity, ProductInfo productInfo);

    Cart findByUserEntityAndProductInfo(UserEntity userEntity, ProductInfo productInfo);

}
