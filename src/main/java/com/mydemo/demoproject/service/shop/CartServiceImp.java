package com.mydemo.demoproject.service.shop;

import com.mydemo.demoproject.Entity.*;
import com.mydemo.demoproject.Repository.admin.CategoryRepo;
import com.mydemo.demoproject.Repository.shop.CartRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CartServiceImp implements CartService{

    @Autowired
    CategoryRepo categoryRepo;

    @Autowired
    CartRepo cartRepo;

    @Override
    public Optional<CategoryInfo> getCategoryDetails(UUID uuid) {
        return categoryRepo.findById(uuid);
    }

    @Override
    public List<Cart> getCartItems() {
      List<Cart> cartList= cartRepo.findAll();
       return cartList;
    }

    @Override
    public Optional<Cart> getCartById(UUID uuid) {
        Optional<Cart> cartList=cartRepo.findById(uuid);
        return cartList;
    }



    @Override
    public void deleteProduct(UUID uuid) {
        cartRepo.deleteById(uuid);
    }

    @Override
    public void saveToCart(Cart cart) {
          cartRepo.save(cart);
    }

    @Override
    public List<Cart> findByUserEntity_Usernames(String username) {

   List<Cart> cartList= cartRepo.findByUserEntity_Username(username);
        return cartList;
    }

//    buy single product
    @Override
    public Cart findByProductInfo_Uuid(UUID productUuid) {
        return cartRepo.findByProductInfo_Uuid(productUuid);
    }




    /*new remove*/
    @Override
    public void removeFromCart(String username, UUID productId) {
        List<Cart> cartItems = cartRepo.findByUserEntity_UsernameAndProductInfo_Uuid(username, productId);
        if (!cartItems.isEmpty()) {
            // Find the cart item that matches the product UUID
            Cart cartItemToRemove = null;
            for (Cart cartItem : cartItems) {
                if (cartItem.getProductInfo().getUuid().equals(productId)) {
                    cartItemToRemove = cartItem;
                    break; // Exit the loop after finding a match
                }
            }

            if (cartItemToRemove != null) {
                // Remove the found cart item
                cartRepo.delete(cartItemToRemove);
            }
        }
    }
    /*changes float to double*/
    @Override
    public Double findTotal(List<Cart> cartItems) {
        Double total = (double) 0;
        for (Cart cart : cartItems) {
            total += cart.getQuantity() * cart.getProductInfo().getPrice();

        }
        return total;
    }

    @Override
    public Optional<ProductInfo> getProduct(UUID uuid) {
        Optional<Cart> cartItem = cartRepo.findById(uuid);
        // Check if the wishlist item with the given UUID exists
        if (cartItem.isPresent()) {
            ProductInfo productInfo = cartItem.get().getProductInfo();
            return Optional.ofNullable(productInfo);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Cart findCartItem(UserEntity userEntity, ProductInfo productInfo) {
        return cartRepo.findByUserEntityAndProductInfo(userEntity, productInfo);
    }


}
