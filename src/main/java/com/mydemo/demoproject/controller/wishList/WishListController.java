package com.mydemo.demoproject.controller.wishList;

import com.mydemo.demoproject.Entity.*;
import com.mydemo.demoproject.service.admin.product.ImageService;
import com.mydemo.demoproject.service.admin.product.ProductService;
import com.mydemo.demoproject.service.shop.CartService;
import com.mydemo.demoproject.service.shop.WishListServiceImp;
import com.mydemo.demoproject.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Controller
@RequestMapping("/wishlist")
public class WishListController {

    @Autowired
    WishListServiceImp wishListService;

    @Autowired
    ImageService imageService;

    @Autowired
    UserService userService;

    @Autowired
    CartService cartService;

    @Autowired
    ProductService productService;

    /*new add to wishlist*/
    @GetMapping("/moveToWishlist/{uuid}")
    public String addToCart(@PathVariable UUID uuid) {
        ProductInfo productInfo = productService.getProduct(uuid).orElse(null);
        if (productInfo != null) {
            WishList wishList = new WishList();
            wishList.setProductInfo(productInfo);
            wishListService.moveToWishList(wishList);
        }
        return "redirect:/wishlist/viewWishlist";
    }

    /*view wishlist*/

    @GetMapping("/viewWishlist")
    public String viewCart(Model model , @AuthenticationPrincipal(expression = "username") String username) {
        List<WishList> wishListItems = wishListService.getWishListItems();

        Optional<UserEntity> user=userService.getUserdata(username);
          model.addAttribute("wishListItems", wishListItems);
        model.addAttribute("user",user);

        return "shop/wishlist";
    }
    /*end*/

    /*@Remove Product from Cart or delete product*/

    @GetMapping("/removeFromWishlist/{uuid}")
    public String removeProduct(@PathVariable UUID uuid) {
        wishListService.deleteProduct(uuid);
        return "redirect:/wishlist/viewWishlist";
    }


    /*Add to cart remove form wishlist*/
    @GetMapping("/addToCartRemoveFromWishlist/{uuid}")
    public String addRemove(@PathVariable UUID uuid,
                            @AuthenticationPrincipal(expression = "username") String username) {
        ProductInfo productInfo = wishListService.getProduct(uuid).orElse(null);
        if (productInfo != null) {
            Cart cartItem = new Cart();
            cartItem.setProductInfo(productInfo);
            cartItem.setUserEntity(userService.getUserdata(username).orElse(null));

            cartService.saveToCart(cartItem);
            wishListService.deleteProduct(uuid);

        }
        return "redirect:/wishlist/viewWishlist";
    }
}
