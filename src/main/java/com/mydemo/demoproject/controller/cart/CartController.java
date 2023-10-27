package com.mydemo.demoproject.controller.cart;


import com.mydemo.demoproject.Entity.*;
import com.mydemo.demoproject.service.admin.offer.OfferService;
import com.mydemo.demoproject.service.admin.product.ImageService;
import com.mydemo.demoproject.service.admin.product.ProductService;
import com.mydemo.demoproject.service.shop.CartService;
import com.mydemo.demoproject.service.shop.ShopService;
import com.mydemo.demoproject.service.shop.WishListService;
import com.mydemo.demoproject.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.WatchService;
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

    @Autowired
    OfferService offerService;


    @PostMapping("/showProductByCategory/{uuid}")
    public String cartView(@PathVariable UUID uuid, Model model) {

        Optional<CategoryInfo> cartInfo = cartService.getCategoryDetails(uuid);
        List<Image> images = imageService.findAllImage();
        model.addAttribute("cartInfo", cartInfo);
        model.addAttribute("images", images);
        return "shop/cart";

    }


    /*NEW UPDATE*/
    @GetMapping("/addToCart/{uuid}")
    public String addToCart(@PathVariable UUID uuid,
                            @RequestParam(name = "discountedPrice", required = false) Float discountPrice,
                            @AuthenticationPrincipal(expression = "username") String username,
                            Model model) {

        ProductInfo productInfo = productService.getProduct(uuid).orElse(null);

        UserEntity user = userService.findByUsernames(username);

        Long currentStocks = productInfo.getStock();
        if (currentStocks >= 1) {
            Cart existingCartItem = cartService.findCartItem(user, productInfo);

            if (existingCartItem != null) {

                existingCartItem.setQuantity(existingCartItem.getQuantity() + 1);
                cartService.saveToCart(existingCartItem);
            } else {
                Cart cartItem = new Cart();
                cartItem.setProductInfo(productInfo);
                cartItem.setUserEntity(user);
                cartItem.setQuantity(1);
                cartService.saveToCart(cartItem);

            }
            productInfo.setDiscountedPrice(discountPrice);
            productService.update(productInfo);
        }
        model.addAttribute("discountPrice", discountPrice);
        return "redirect:/cart/viewCart";

    }
            /*end*/



    @GetMapping("/viewCart")
    public String viewCart(Model model, @AuthenticationPrincipal(expression = "username") String username) {
        List<Cart> cartItems = cartService.findByUserEntity_Usernames(username);
        /*new*/

        if (cartItems.isEmpty()) {
            model.addAttribute("cartEmpty", true);
        } else {
            Optional<UserEntity> user = userService.getUserdata(username);

            float totalDiscountPrice = 0.0f;
            for (Cart cartItem : cartItems) {

                ProductInfo productInfo = cartItem.getProductInfo();

                CategoryInfo category = productInfo.getCategory();

                int productQuantity = cartItem.getQuantity();
                String productName = cartItem.getProductInfo().getName();


                List<Offer> categoryOffers = offerService.getCategoryOffers(category);

                List<Offer> productOff = offerService.getProductOffers(productInfo);

                float discountPrice = 0f;

                for (Offer productWiseOffer : productOff) {
                    for (Offer categoryOffer : categoryOffers) {
                        if (productWiseOffer.getCategoryOffPercentage() > categoryOffer.getCategoryOffPercentage()) {

                            discountPrice = productInfo.getPrice() - (productInfo.getPrice() * productWiseOffer.getCategoryOffPercentage() / 100);

                            totalDiscountPrice += discountPrice * productQuantity;

                        } else {

                            discountPrice = productInfo.getPrice() - (productInfo.getPrice() * categoryOffer.getCategoryOffPercentage() / 100);

                            totalDiscountPrice += discountPrice * productQuantity;

                        }

                    }
                }

            }
            model.addAttribute("totalDiscountPrice", totalDiscountPrice);
            model.addAttribute("total", cartService.findTotal(cartItems));
            model.addAttribute("cartItems", cartItems);
            model.addAttribute("user", user.orElse(null));
        }
        return "shop/cart";
    }

        /*end*/

    @GetMapping("/removeFromCart/{uuid}")
    public String removeProduct(@PathVariable UUID uuid,
                                @AuthenticationPrincipal(expression = "username") String username ) {

        cartService.removeFromCart(username,uuid);
        return "redirect:/cart/viewCart";
    }


    @PostMapping("/setQuantity")
    public String setQuantity(@RequestParam UUID uuid, @RequestParam int quantity,
                              @RequestParam float price) {

        Optional<Cart> optionalCartItem = cartService.getCartById(uuid);

        if (optionalCartItem.isPresent()) {

            Cart cartItem = optionalCartItem.get();
            cartItem.setQuantity(quantity);
            cartItem.getProductInfo().setDiscountedPrice(price);
            cartService.saveToCart(cartItem);
        }

        return "redirect:/cart/viewCart";
    }


    /*addToWishlistRemoveFromCart*/
    @GetMapping("/addToWishlistRemoveFromCart/{uuid}")
    public String addRemove(@PathVariable UUID uuid,
                            @AuthenticationPrincipal(expression = "username") String username) {
        ProductInfo productInfo = cartService.getProduct(uuid).orElse(null);

        if (productInfo != null) {

            WishList wishList = new WishList();
            wishList.setProductInfo(productInfo);
            wishList.setUserEntity(userService.getUserdata(username).orElse(null));

            wishListService.moveToWishList(wishList);
            System.out.println(uuid);
            cartService.deleteProduct(uuid);

        } else {
            System.out.println("Product with UUID " + uuid + " not found.");
        }
        return "redirect:/cart/viewCart";
    }

}
