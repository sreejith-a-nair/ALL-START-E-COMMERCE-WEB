package com.mydemo.demoproject.controller.cart;


import com.mydemo.demoproject.Entity.*;
import com.mydemo.demoproject.service.admin.product.ImageService;
import com.mydemo.demoproject.service.admin.product.ProductService;
import com.mydemo.demoproject.service.shop.CartService;
import com.mydemo.demoproject.service.shop.WishListService;
import com.mydemo.demoproject.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    CartService cartService;

    @Autowired
    ImageService imageService;

    @Autowired
    ProductService productService;

    @Autowired
    UserService userService;

    @Autowired
    WishListService wishListService;

    @PostMapping("/showProductByCategory/{uuid}")
    public String cartView(@PathVariable UUID uuid, Model model) {
        System.out.println("cart view.........................................................................." + uuid);
        Optional<CategoryInfo> cartInfo = cartService.getCategoryDetails(uuid);
        List<Image> images = imageService.findAllImage();
        System.out.println("cartInfo>>>>>>>>>>>>>>>>>>>>>>>>" + cartInfo);
        model.addAttribute("cartInfo", cartInfo);
        model.addAttribute("images", images);
        return "shop/cart";

    }

    /* @get Show cart */
//    @GetMapping("/addToCart/{uuid}")
//    public String showSingleProduct(@PathVariable UUID uuid,Model model) {
////        System.out.println("Single product ID:cart]]]] " + uuid);
//
////        cartService.saveToCart(uuid);
//        Optional<ProductInfo>productInfo=productService.getProduct(uuid);
////       System.out.println("Single product ID: >>>>>>" + productInfo);
//
////        List<Image>images=imageService.findAllImage();
////        model.addAttribute("images",images);
//        model.addAttribute("productInfo",productInfo.orElse(null));
//
////        System.out.println("Image ID: -----------------" + images);
////        List<Cart> cartInfo=cartService.getCartInfo();
////        System.out.println("Cart info======================================="+cartInfo);
////        model.addAttribute("images",images);
////        model.addAttribute("cartInfo",cartInfo);
////        model.addAttribute("productInfo",productInfo.orElse(null));
//        return "shop/cart";
//    }


    @GetMapping("/addToCart/{uuid}")
    public String addToCart(@PathVariable UUID uuid,
                            @AuthenticationPrincipal(expression = "username") String username,
                           Model model) {
        System.out.println("hail cart+"+uuid);
        ProductInfo productInfo = productService.getProduct(uuid).orElse(null);
        System.out.println("productInfo"+productInfo);
        UserEntity user = userService.findByUsernames(username);
        System.out.println("user"+user);

        Long currentStock= productInfo.getStock();
        System.out.println("currentStock>>>>>>>>>>"+currentStock);
        if(currentStock>=1) {


            if (productInfo != null && user != null) {

                Cart existingCartItem = cartService.findCartItem(user, productInfo);
                System.out.println("existingCartItem>>>>>>>>>>>>>>" + existingCartItem);
                if (existingCartItem != null) {
                    System.out.println("existingCartItem>>>>>>>>>>>>>>>>>>>>>>>>" + existingCartItem);
                    existingCartItem.setQuantity(existingCartItem.getQuantity() + 1);
                    cartService.saveToCart(existingCartItem);
                } else {
                    System.out.println("product no null");
                    Cart cartItem = new Cart();
                    cartItem.setProductInfo(productInfo);
                    cartItem.setUserEntity(user);
                    cartItem.setQuantity(1);
//            user.setCartItems((List<Cart>) cartItem);
                    System.out.println("cartlist>>>>>>" + cartItem);
                    cartService.saveToCart(cartItem);
                }

            }
            return "redirect:/cart/viewCart";
        }
        else {
        model.addAttribute("error", "Product is out of stock");
            return "shop/message";
        }


    }

    @GetMapping("/viewCart")
    public String viewCart(Model model, @AuthenticationPrincipal(expression = "username") String username) {
        List<Cart> cartItems = cartService.findByUserEntity_Usernames(username);

        if (cartItems.isEmpty()) {
            model.addAttribute("cartEmpty", true);
        } else {
            System.out.println("cart after delete[[[[[" + cartItems);
            Optional<UserEntity> user = userService.getUserdata(username);
            System.out.println("user/////" + user);
            model.addAttribute("total", cartService.findTotal(cartItems));
            model.addAttribute("cartItems", cartItems);
            model.addAttribute("user", user.orElse(null));
        }

        return "shop/cart";
    }

    /*@Remove Product from Cart or delete product*/
    @GetMapping("/removeFromCart/{uuid}")
    public String removeProduct(@PathVariable UUID uuid,
                                @AuthenticationPrincipal(expression = "username") String username ) {

        cartService.removeFromCart(username,uuid);
        System.out.println("product uuid///////" + uuid);

        return "redirect:/cart/viewCart";
    }

    /*soft delete*/
//    @GetMapping("/removeFromCart/{uuid}")
//    public String removeProduct(@PathVariable UUID uuid) {
//        Optional<Cart> cartItem = cartService.getCartById(uuid);
//
//        if (cartItem.isPresent()) {
//            // Update the deletedById property to mark it as deleted.
//            cartItem.get().setDeletedById(true);
//            cartService.saveToCart(cartItem);
//            System.out.println("Product with UUID " + uuid + " removed from cart.");
//        } else {
//            System.out.println("Product with UUID " + uuid + " not found in the cart.");
//        }
//
//        return "redirect:/cart/viewCart";
//    }

    /*Quantity setting*/
    @PostMapping("/setQuantity")
    public String SetQuantity(@RequestParam("quantity") int quantity,
                              @RequestParam("uuid") UUID cartId) {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>cartId" + cartId);
        Cart cartInfo = cartService.getCartById(cartId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        System.out.println(";;;;;;;;;;;;;;;;;;cartId" + cartInfo);
        cartInfo.setQuantity(quantity);
//        cartInfo.setQuantity(1);
        System.out.println("new cart entity..." + cartInfo);
        cartService.saveToCart(cartInfo);
        System.out.println("setQuantity>>>>>>>>>>>>>" + quantity);

        return "redirect:/cart/viewCart";
    }

    /*addToWishlistRemoveFromCart*/
    @GetMapping("/addToWishlistRemoveFromCart/{uuid}")
    public String addRemove(@PathVariable UUID uuid,
                            @AuthenticationPrincipal(expression = "username") String username) {
        ProductInfo productInfo = cartService.getProduct(uuid).orElse(null);
        System.out.println("product info''''''" + productInfo);
        if (productInfo != null) {
//            cartService.deleteProduct(uuid);
            WishList wishList = new WishList();
            wishList.setProductInfo(productInfo);
            wishList.setUserEntity(userService.getUserdata(username).orElse(null));

            wishListService.moveToWishList(wishList);
            System.out.println(uuid);
            cartService.deleteProduct(uuid);
            System.out.println("ProcartService.deleteProduct(uuid);duct with UUID " + uuid + " moved to wishlist and removed from cart.");
        } else {

            System.out.println("Product with UUID " + uuid + " not found.");

        }
        return "redirect:/cart/viewCart";
    }

}
