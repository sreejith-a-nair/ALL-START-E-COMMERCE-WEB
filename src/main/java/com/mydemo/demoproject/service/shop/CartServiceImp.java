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

//    @Override
//    public List<Cart> getCartInfo() {
//        return cartRepo.findAll();
//    }

    @Override
    public void deleteProduct(UUID uuid) {
        System.out.println("service'''''"+uuid);
        cartRepo.deleteById(uuid);
    }

    @Override
    public void saveToCart(Cart cart) {
        System.out.println("cart save");
          cartRepo.save(cart);
    }

    @Override
    public List<Cart> findByUserEntity_Usernames(String username) {

   List<Cart> cartList= cartRepo.findByUserEntity_Username(username);
//        System.out.println("find user ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;"+cartList);
        return cartList;
    }

//    buy single prioduct
    @Override
    public Cart findByProductInfo_Uuid(UUID productUuid) {
        return cartRepo.findByProductInfo_Uuid(productUuid);
    }



/*new update*/
//  @Override
//   public boolean productExistsInCart(UserEntity userEntity, ProductInfo productInfo) {
//    return cartRepo.productExistsInCart(userEntity, productInfo);
//   }


    /*new remove*/
    @Override
    public void removeFromCart(String username, UUID productId) {
        List<Cart> cartItems = cartRepo.findByUserEntity_UsernameAndProductInfo_Uuid(username, productId);
        System.out.println("remove item >>>>>>>>>>>>>>>" + cartItems);
        if (!cartItems.isEmpty()) {
            System.out.println("removeeeeeeeeeeeeeee");
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
                System.out.println("Removed product with UUID: " + productId);
            }
        }
    }
    /*changes float to double*/
    @Override
    public Double findTotal(List<Cart> cartItems) {
        Double total = (double) 0;
        for (Cart cart : cartItems) {
            total += cart.getQuantity() * cart.getProductInfo().getPrice();
//          List <ProductInfo> productInfo= (List<ProductInfo>) cart.getProductInfo();
//           for (ProductInfo products:productInfo){
//              Float discount=  products.getDiscountedPrice();
//               System.out.println("discount>>price ofe all products service in cart"+discount);
//
//           }
        }
        System.out.println("total"+total);
        return total;
    }

    @Override
    public Optional<ProductInfo> getProduct(UUID uuid) {
        Optional<Cart> cartItem = cartRepo.findById(uuid);
        System.out.println("cartItems in service"+cartItem);
        // Check if the wishlist item with the given UUID exists
        if (cartItem.isPresent()) {
            System.out.println("cartItems in service ifff/////"+cartItem);
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
