package com.mydemo.demoproject.controller.order;

import com.mydemo.demoproject.Entity.*;
import com.mydemo.demoproject.Entity.enumlist.CancelReason;
import com.mydemo.demoproject.Entity.enumlist.OrderStatus;
import com.mydemo.demoproject.Entity.enumlist.PaymentMode;
import com.mydemo.demoproject.Entity.enumlist.ReturnReason;
import com.mydemo.demoproject.Repository.admin.OrderItemRepo;
import com.mydemo.demoproject.Repository.admin.OrderRepo;
import com.mydemo.demoproject.Repository.shop.CancelOrderRepo;
import com.mydemo.demoproject.Repository.shop.CartRepo;
import com.mydemo.demoproject.Repository.shop.ReturnOrderRepo;
import com.mydemo.demoproject.service.admin.cartegory.CategoryService;
import com.mydemo.demoproject.service.admin.coupon.CouponService;
import com.mydemo.demoproject.service.admin.offer.OfferService;
import com.mydemo.demoproject.service.admin.order.OrderService;
import com.mydemo.demoproject.service.admin.product.ProductService;
import com.mydemo.demoproject.service.shop.AddressService;
import com.mydemo.demoproject.service.shop.CartService;
import com.mydemo.demoproject.service.shop.ShopService;
import com.mydemo.demoproject.service.user.ReturnOrderService;
import com.mydemo.demoproject.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.nio.channels.FileLock;
import java.nio.file.WatchService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/order")
public class OrderController {
    @Autowired
    AddressService addressService;
    @Autowired
    CartService cartService;

    @Autowired
    OrderService orderService;

    @Autowired
    UserService userService;

    @Autowired
    ProductService productService;

    @Autowired
    CouponService couponService;

    @Autowired
    CartRepo cartRepo;

    @Autowired
    ReturnOrderRepo returnOrderRepo;

    @Autowired
    ReturnOrderService returnOrderService;


    @Autowired
    CancelOrderRepo cancelOrderRepo;

    @Autowired
    OrderItemRepo orderItemsRepo;

    @Autowired
    ShopService shopService;

    @Autowired
    OfferService offerService;

/*...............SHOW ORDER .....................(GET)*/

    @GetMapping("/show/{uuid}")
    public String showOrder(@PathVariable UUID uuid, Model model,
                            @RequestParam(name = "totalDiscountPrice", required = true) float totalDiscountPrice,
                            @AuthenticationPrincipal(expression = "username") String username) {


        Optional<UserEntity> user = userService.getUserdata(username);
        List<Address> useraddresses = user.map(UserEntity::getAddresses).orElse(Collections.emptyList());
        List<Address> enabledAddresses = useraddresses.stream()
                .filter(Address::isEnabled)
                .collect(Collectors.toList());
        List<Cart> cartList = cartService.findByUserEntity_Usernames(username);




        /*new*/
        float discountPrice = 0;
        for (Cart cartItem : cartList) {

            ProductInfo productInfo = cartItem.getProductInfo();
            CategoryInfo categoryInfo=productInfo.getCategory();
               List<Offer> categoryOffers=offerService.getCategoryOffers(categoryInfo);
               List<Offer>productOffers=offerService.getProductOffers(productInfo);


/*new update */
            for (Offer productWiseOffer : productOffers) {
                for (Offer categoryOffer : categoryOffers) {
                    if (productWiseOffer.getCategoryOffPercentage() > categoryOffer.getCategoryOffPercentage()) {

                        discountPrice = productInfo.getPrice() - (productInfo.getPrice() * productWiseOffer.getCategoryOffPercentage() / 100);
                        productInfo.setDiscountedPrice(discountPrice);
                    } else {

                        discountPrice = productInfo.getPrice() - (productInfo.getPrice() * categoryOffer.getCategoryOffPercentage() / 100);
                        productInfo.setDiscountedPrice(discountPrice);
                    }

                }
            }
        }
        /*end*/

        List<Coupon> couponList = couponService.findAll();


        Double totalprice = (double) cartService.findTotal(cartList);

        boolean available;
        if (totalDiscountPrice > 10000) {
            available = true;

        } else {
            available = false;
        }

        Wallet wallet  =shopService.getWalletByUser(username);
        float totalMoney  =wallet.getTotalMoney();

        model.addAttribute("totalMoney",totalMoney);
        model.addAttribute("available", available);
        model.addAttribute("couponList", couponList);
        model.addAttribute("totalDiscountPrice", totalDiscountPrice);
        model.addAttribute("total", totalprice);
        model.addAttribute("discountPrice", discountPrice);
        model.addAttribute("user", user);
        model.addAttribute("useraddresses", enabledAddresses);
        model.addAttribute("cartList", cartList);
        return "shop/order";
    }



    /* ************************END SHOW ORDER    (GET)  ***********************************/




 /*.......................SHOW SINGLE PRODUCT UUID (GET)........................*/
    @GetMapping("/show-single-product/{productId}/{quantity}")
    public String showSingleOrder(
            @PathVariable("productId") UUID productId,
            @PathVariable("quantity") float quantity,
            @RequestParam(name = "discountedPrice", required = false) Float discountedPrice,
            Model model,
            @AuthenticationPrincipal(expression = "username") String username,
            HttpSession session) {

        float totalDiscountPrice=(discountedPrice*quantity);

        /*ADDRESS FILTERING*/
        Optional<UserEntity> user = userService.getUserdata(username);
        List<Address> userAddresses = user.map(UserEntity::getAddresses).orElse(Collections.emptyList());
        List<Address> enabledAddresses = userAddresses.stream()
                .filter(Address::isEnabled)
                .collect(Collectors.toList());

        float discountPrice = discountedPrice;

        Optional<ProductInfo> product = productService.getProduct(productId);


        ProductInfo products =product.get();

        float productPrice=products.getPrice();
        List<Offer> offer=products.getOffer();
        int productOffer=0;
        for (Offer  productBaseOffer : offer){
            productOffer  =   productBaseOffer.getCategoryOffPercentage();
        }


        List<Coupon> couponList = couponService.findAll();
    session.setAttribute("productUUID", productId);
    session.setAttribute("productQuantity",quantity);
    session.setAttribute("user", user);
    /*new*/
    boolean available;
    if(totalDiscountPrice>10000){
        available=true;
         }
    else{available=false;
       }

        Wallet wallet  =shopService.getWalletByUser(username);
        float totalMoney  =wallet.getTotalMoney();

        model.addAttribute("totalMoney",totalMoney);
        model.addAttribute("available",available);
        model.addAttribute("discountPrice",discountPrice);
        model.addAttribute("couponList", couponList);
        model.addAttribute("quantity" + quantity);
        model.addAttribute("totalDiscountPrice",totalDiscountPrice);
        model.addAttribute("user", user);
        model.addAttribute("userAddresses", enabledAddresses);
        model.addAttribute("product", product.orElse(null));

        return "shop/single-order";
    }
/* **************************** END SHOW SINGLE PRODUCT (GET)  ********************************/




    /*.....................APPLY SINGLE ORDER COUPON..(POST)....BUY-NOW ................*/
    @PostMapping("/applySingleOrderCoupon")
    /*End  Apply single order coupon*/
    public String applySingleOrderCoupon(@RequestParam(name = "couponCode") String couponCode,
                                         @RequestParam(name = "totalDiscountPrice") float totalDiscountAmount,
                                         @AuthenticationPrincipal(expression = "username") String username,
                                         Model model, HttpSession session) {
        session.setAttribute("couponCode", couponCode);


        Optional<UUID> productUUIDOptional = Optional.ofNullable((UUID) session.getAttribute("productUUID"));
        UUID productInfo=productUUIDOptional.get();
      Optional<ProductInfo> productData =productService.getProduct(productInfo);
      ProductInfo productInfo1=productData.get();

      float discountPrice =productInfo1.getDiscountedPrice();
        float quantity = (float) session.getAttribute("productQuantity");

        Optional<UserEntity> user = (Optional<UserEntity>) session.getAttribute("user");

        List<Address> userAddresses = user.map(UserEntity::getAddresses).orElse(Collections.emptyList());
        List<Address> enabledAddresses = userAddresses.stream()
                .filter(Address::isEnabled)
                .collect(Collectors.toList());

        /*find coupon by code*/
        List<Coupon> coupons = orderService.findCouponByCode(couponCode);

        List<Coupon> couponList = couponService.findAll();

        UUID productId = productUUIDOptional.get();
        Optional<ProductInfo> product = productService.getProduct(productId);

        float oldtotal =0f;
            ProductInfo productPrice = product.get();
            long price = (long) productPrice.getPrice();

            oldtotal = orderService.findTotalUseQuantity(price, quantity);


        Double totalDiscountPrice=0d;

        boolean available;
        if(discountPrice>10000){
            totalDiscountPrice = orderService.applySingleCouponFindTotal(coupons, username, totalDiscountAmount);
            available=true;

        }
        else{available=false;
        }
        System.out.println(totalDiscountPrice);
            ArrayList allCoupon = new ArrayList();

            Wallet wallet  =shopService.getWalletByUser(username);
            float totalMoney  =wallet.getTotalMoney();

            model.addAttribute("totalMoney",totalMoney);
            model.addAttribute("available",available);
            model.addAttribute("couponApplied", true);
            model.addAttribute("quantity" , quantity);
            model.addAttribute("discountPrice",discountPrice);
            model.addAttribute("totalDiscountPrice", totalDiscountPrice);
            model.addAttribute("user", user);
            model.addAttribute("userAddresses", enabledAddresses);
            model.addAttribute("product", product.orElse(null));

            return "shop/single-order";
    }
    /* ***************************** END APPLY SINGLE ORDER COUPON..(POST)....BUY-NOW ********************************* */




  /*.....................APPLY COUPON..(POST)....CHECK-OUT ................*/
    @PostMapping("/applyCoupon")
    public String applyCoupon(@RequestParam(name = "couponCode") String couponCode,
                              @RequestParam(name = "totalDiscountPrice") float totalDiscountPrices,
                              @AuthenticationPrincipal(expression = "username") String username,
                              Model model, HttpSession session) {

        System.out.println("TOTAL DISCOUNT PRICE IN APPLY COUPON POST     "+totalDiscountPrices);
        session.setAttribute("couponCode", couponCode);
        List<Cart> cartList = cartRepo.findByUserEntity_Username(username);

        List<Coupon> coupons = orderService.findCouponByCode(couponCode);

        /*address filter*/
        Optional<UserEntity> user = userService.getUserdata(username);
        List<Address> useraddresses = user.map(UserEntity::getAddresses).orElse(Collections.emptyList());
        List<Address> enabledAddresses = useraddresses.stream()
                .filter(Address::isEnabled)
                .collect(Collectors.toList());

        List<Coupon> couponlist = couponService.findAll();

        Double existTotal = 0d;
        for (Cart cart : cartList) {
            existTotal += cart.getQuantity() * cart.getProductInfo().getPrice();
        }

        float totalDiscountPrice = orderService.applyCouponFindTotal(coupons, username,totalDiscountPrices);

        boolean available;
        if(totalDiscountPrice>10000){available=true;
        }
        else{available=false;
        }

            ArrayList couponList = new ArrayList();

            Wallet wallet  =shopService.getWalletByUser(username);
            float totalMoney  =wallet.getTotalMoney();

            model.addAttribute("totalMoney",totalMoney);
            model.addAttribute("available",available);
            model.addAttribute("couponApplied", true);
            model.addAttribute("couponList", couponList);
            model.addAttribute("user", user);
            model.addAttribute("totalDiscountPrice", totalDiscountPrice);
            model.addAttribute("useraddresses", enabledAddresses);
            model.addAttribute("cartList", cartList);
            return "shop/order";

    }
/* **************************** END APPLY COUPON (POST) CHECK - OUT  **************************/





    /*.......................................CREATE ORDER (GET)...........................................................*/
    @PostMapping("/submit-selected-address-and-payment")
    public String submitOrder(@RequestParam("selectedAddress") UUID selectedAddress,
                              @RequestParam("paymentMethod") PaymentMode selectedPaymentOption,
                              @RequestParam("totalDiscountPrice") float totalDiscountPrice,
                              @AuthenticationPrincipal(expression = "username") String username,
                              Model model, HttpSession session) {
       Double total= (double) totalDiscountPrice;
        System.out.println(total);
        String couponCode = (String) session.getAttribute("couponCode");


        List<Coupon> coupons = orderService.findCouponByCode(couponCode);

        Optional<UserEntity> userDetails = orderService.getUserDataById(username);

        UserEntity userEntity = userDetails.get();

        List<Cart> cartList = orderService.getCartByUser(username);

        Double totalPrice = cartList.stream().mapToDouble(
                cartItems -> cartItems.getProductInfo().getPrice() * cartItems.getQuantity()).sum();
        Optional<Address> address = orderService.getAddressById(selectedAddress);

        Address userAddresses = address.get();



        /*......WALLET PAYMENT..... */

        if (selectedPaymentOption == PaymentMode.WALLET){

            /*check single odr or not*/
            if (total.equals(totalPrice)){
//                TransactionDetails transactionDetails = createTransaction(totalPrice);
                if (userAddresses==null) {

                    model.addAttribute("error", "Please add an address before proceeding.");
                    return "redirect:/addresses";
                }

                Wallet wallet  =shopService.getWalletByUser(username);
                float  walletTotalMoney =  wallet.getTotalMoney();

                float walletPay= orderService.walletPay(total,walletTotalMoney);
                orderService.updateWallet(walletPay,username,total);

                Order createOrder = orderService.createOrder(userAddresses, total,totalPrice, userEntity, selectedPaymentOption, cartList, username,coupons);
                UUID savedUuid = createOrder.getUuid();
                Optional<Order> order = orderService.findOrderById(savedUuid);
                Order recentOrder = order.get();
                List<OrderItems> orderItem = recentOrder.getOrderItems();
                orderService.removeProductFromCart(orderItem, cartList);

                ProductInfo productInfo1;
                for (OrderItems orderProduct : orderItem) {
                    productInfo1 = orderProduct.getProductInfo();
                    UUID orderProductUuid=  productInfo1.getUuid();
                    Integer orderQuantity=orderProduct.getQuantity();
                    Optional<ProductInfo> existProduct=productService.getProduct(orderProductUuid);
                    UUID existProductUuid=existProduct.get().getUuid();
                    if(existProductUuid.equals(orderProductUuid)){
                        Long newStock= orderService.updateStockOrder(orderQuantity,  existProductUuid) ;
                        ProductInfo product=existProduct.get();
                        product.setStock(newStock);
                        productService.update(product);
                    }
                }
                model.addAttribute("username", username);

                return "shop/order-confirmation";


            }else {
                /*check out not a single order*/
//                TransactionDetails transactionDetails = createTransaction(total);
                if (userAddresses==null) {

                    model.addAttribute("error", "Please add an address before proceeding.");
                    return "redirect:/addresses";
                }


               Wallet wallet  =shopService.getWalletByUser(username);
               float  walletTotalMoney =  wallet.getTotalMoney();

                float walletPay=  orderService.walletPay(total,walletTotalMoney);
                orderService.updateWallet(walletPay,username,total);


                Order createOrder = orderService.createOrder(userAddresses, total,totalPrice, userEntity, selectedPaymentOption, cartList, username,coupons);

                /*find order*/
                UUID savedUuid = createOrder.getUuid();
                Optional<Order> order = orderService.findOrderById(savedUuid);
                Order recentOrder = order.get();
                List<OrderItems> orderItem = recentOrder.getOrderItems();

                orderService.removeProductFromCart(orderItem, cartList);

                ProductInfo productInfo1;
                for (OrderItems orderProduct : orderItem) {

                    productInfo1 = orderProduct.getProductInfo();
                    UUID orderProductUuid=  productInfo1.getUuid();

                    Integer orderQuantity=orderProduct.getQuantity();
                    Optional<ProductInfo> existProduct=productService.getProduct(orderProductUuid);

                    UUID existProductUuid=existProduct.get().getUuid();

                    if(existProductUuid.equals(orderProductUuid)){

                        Long newStock= orderService.updateStockOrder(orderQuantity,  existProductUuid) ;
                        ProductInfo product=existProduct.get();
                        product.setStock(newStock);
                        productService.update(product);
                    }
                }

                model.addAttribute("username", username);

                return"shop/order-confirmation";

            }

        }
            /*....END WALLET PAYMENT.........*/


        /*,,,,,,,RAZORPAY,,,,,,,,,,,*/
        if (selectedPaymentOption == PaymentMode.RAZORPAY) {
            if (total.equals(totalPrice)) {
                TransactionDetails transactionDetails = createTransaction(totalPrice);

                if (userAddresses==null) {

                    model.addAttribute("error", "Please add an address before proceeding.");
                    return "redirect:/addresses";
                }

                Order createOrder = orderService.createOrder(userAddresses, total,totalPrice, userEntity, selectedPaymentOption, cartList, username,coupons);

                UUID savedUuid = createOrder.getUuid();
                Optional<Order> order = orderService.findOrderById(savedUuid);
                Order recentOrder = order.get();
                List<OrderItems> orderItem = recentOrder.getOrderItems();

                orderService.removeProductFromCart(orderItem, cartList);


                ProductInfo productInfo1;
                for (OrderItems orderProduct : orderItem) {

                    productInfo1 = orderProduct.getProductInfo();
                    UUID orderProductUuid=  productInfo1.getUuid();

                    Integer orderQuantity=orderProduct.getQuantity();
                    Optional<ProductInfo> existProduct=productService.getProduct(orderProductUuid);

                    UUID existProductUuid=existProduct.get().getUuid();

                    if(existProductUuid.equals(orderProductUuid)){

                        Long newStock= orderService.updateStockOrder(orderQuantity,  existProductUuid) ;
                        ProductInfo product=existProduct.get();
                        product.setStock(newStock);
                        productService.update(product);
                    }
                }

                model.addAttribute("transactionDetails", transactionDetails);

                return "shop/razorpay";

            }else {

                TransactionDetails transactionDetails = createTransaction(total);
                if (userAddresses==null) {

                    model.addAttribute("error", "Please add an address before proceeding.");
                    return "redirect:/addresses";
                }

                Order createOrder = orderService.createOrder(userAddresses, total,totalPrice, userEntity, selectedPaymentOption, cartList, username,coupons);

                UUID savedUuid = createOrder.getUuid();
                Optional<Order> order = orderService.findOrderById(savedUuid);
                Order recentOrder = order.get();
                List<OrderItems> orderItem = recentOrder.getOrderItems();
                orderService.removeProductFromCart(orderItem, cartList);

                ProductInfo productInfo1;
                for (OrderItems orderProduct : orderItem) {

                    productInfo1 = orderProduct.getProductInfo();
                    UUID orderProductUuid=  productInfo1.getUuid();

                    Integer orderQuantity=orderProduct.getQuantity();
                    Optional<ProductInfo> existProduct=productService.getProduct(orderProductUuid);

                    UUID existProductUuid=existProduct.get().getUuid();

                    if(existProductUuid.equals(orderProductUuid)){

                        Long newStock= orderService.updateStockOrder(orderQuantity,  existProductUuid) ;
                        ProductInfo product=existProduct.get();
                        product.setStock(newStock);
                        productService.update(product);
                    }
                }

                model.addAttribute("transactionDetails", transactionDetails);

                return "shop/razorpay";
            }

        }
        /*,,,,,END,,RAZORPAY,,,,,,,,,,,*/


        Order createOrder = orderService.createOrder(userAddresses, total,totalPrice, userEntity, selectedPaymentOption, cartList, username,coupons);

        Order saveOrder = orderService.saveOrder(createOrder);
        UUID savedOrderUuid = saveOrder.getUuid();

        Optional<Order> order = orderService.findOrderById(savedOrderUuid);
        Order recentOrder = order.get();
        List<OrderItems> orderItems = recentOrder.getOrderItems();

        /*new update*/
        for (OrderItems orderItem : orderItems) {
            ProductInfo productData = orderItem.getProductInfo();
            UUID orderProductUuid = productData.getUuid();
            Integer orderQuantity=orderItem.getQuantity();
           Optional<ProductInfo> existProduct=productService.getProduct(orderProductUuid);

           UUID existProductUuid=existProduct.get().getUuid();

             if(existProductUuid.equals(orderProductUuid)){

                Long newStock= orderService.updateStockOrder(orderQuantity,  existProductUuid) ;
                ProductInfo product=existProduct.get();
                product.setStock(newStock);
                productService.update(product);
             }
        }
        orderService.removeProductFromCart(orderItems, cartList);
        model.addAttribute("username", username);
        return "shop/order-confirmation";

    }
    /* ******************************************* END CREATE ORDER (POST) ************************************ */


    /*........................MY ORDER (GET).......................*/
    @GetMapping("/myOrder")
    public String  myOrder( @AuthenticationPrincipal(expression = "username") String username,
                            Model model){

        List<Order>myOrders=orderService.getOrderByUsername(username);
        String currentOrderStatus = myOrders.stream()
                .findFirst()
                .map(order -> order.getOrderStatus().toString())
                .orElse("No orders found"); //
        System.out.println(" currentOrderStatus   =  "+currentOrderStatus);

        model.addAttribute("myOrders",myOrders);
   return  "shop/my-order";
    }
    /* ************************ END MY ORDER (GET) ******************** */




    /*........................UPDATE ORDER STATUS (POST).......................*/

    @PostMapping("/updateStatus")
    public String updateStatus(@RequestParam("selectedStatus") OrderStatus  selectedStatus,
                               @RequestParam("orderId") UUID orderId) {

        System.out.println("selectedStatu.....s"+selectedStatus);
        Optional<Order> order = orderService.findOrderById(orderId);

        if (order.isPresent()) {
            Order orderStatus=order.get();
            orderStatus.setOrderStatus(selectedStatus);
            if(selectedStatus.equals(OrderStatus.DELIVERED)){
                orderStatus.setDeliveryDate(LocalDate.now());
            }
            orderService.saveOrder(orderStatus);
        }

        return "redirect:/order/admin";
    }


    /* ************************ END UPDATE ORDER STATUS (POST) ******************** */


    /*......................../ADMIN PAGINATION  PAGE NUMBER (GET).......................*/
    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String showAllOrder(Model model) {
        return findPaginated(1, model);
    }


    @GetMapping("/page/{pageNo}")
    public String findPaginated(@PathVariable(value = "pageNo") int pageNo, Model model) {
        int pageSize = 5;
        Page<Order> page = orderService.findPaginated(pageNo, pageSize);
        List<Order> orders = page.getContent();

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("orders", orders);

        return "admin/order-list";
    }
    /* ************************ END PAGINATION NUMBER (GET) ******************** */



    /*........................PAGINATION UUID (GET).......................*/

    @GetMapping("/pages/{uuid}")
    public String findPaginate(@PathVariable("uuid") UUID uuid, Model model) {

        Optional<Order> order = orderService.findOrderById(uuid);


        List<OrderItems> orderItems = order.get().getOrderItems();

        model.addAttribute("orderItems", orderItems);
        model.addAttribute("order", order.get());

        return "admin/order-detail";
    }
    /* ************************ END PAGINATION UUID (GET) ******************** */



    /*........................CREATE TRANSACTION (GET).......................*/
    @PostMapping("/createTransaction/{amount}")
    @PreAuthorize("hasRole('Admin')")
    public TransactionDetails createTransaction(@PathVariable(name = "amount") Double amount) {
        return orderService.createTransaction(amount);

    }
    /* ************************ END CREATE TRANSACTION (GET) ******************** */



    /*........................PAYMENT SUCCESS PAGE (GET).......................*/
    @GetMapping("/payment/success")
    public String handlePaymentSuccess() {
        System.out.println("razorr oredr  pay suuces");

        return "shop/success";
    }
    /* ************************ END PAYMENT SUCCESS PAGE (GET) ******************** */



    /*........................SEARCH ORDER (GET).......................*/
    @GetMapping(value = "/search", params = "keyword")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String searchOrderUser(Model model, @Param("keyword") String keyword) {
        try {
            List<Order> orders;

            if (keyword != null && !keyword.isEmpty()) {
                orders = orderService.searchOrderUserName(keyword);
            } else {

                orders = orderService.loadAllUser();
            }

            model.addAttribute("orders", orders);

            return "admin/order-list";

        } catch (Exception e) {

            e.printStackTrace();

            model.addAttribute("errorMessage", "An error occurred while processing your request.");
            return "error";
        }

    }
    /* **********************************************  END SEARCH ORDER (GET) ********************************** */



    /*........................UPDATE  ADDRESS (POST).......................*/
    @PostMapping("/edit")
    public String  updatesAddress(@RequestParam("uuid") UUID uuid,
                                  @RequestParam("houseNumberOrName") String houseNumberOrName,
                                  @RequestParam("area") String area,
                                  @RequestParam("city") String city,
                                  @RequestParam("district") String district,
                                  @RequestParam("state") String state,
                                  @RequestParam("pincode") Long pincode,
                                  @RequestParam("landmark")String landmark){
        System.out.println("edit post here>>>>"+uuid);
        System.out.println("addressUuid = "+houseNumberOrName);


        Optional<Address> address= addressService.findAddressById(uuid);
        if(address.isPresent()){
            Address addressList =address.get();

            addressList.setUuid(uuid);
            addressList.setHouseNumberOrName(houseNumberOrName);
            addressList.setCity(city);
            addressList.setArea(area);
            addressList.setPincode(pincode);
            addressList.setDistrict(district);
            addressList.setLandmark(landmark);
            addressList.setState(state);
//            addressList.setEnabled();
            addressService.saveToAddress(addressList);
        }

        return  "redirect:/address/show";
    }
    /* **********************************************  END UPDATE ADDRESS (POST) ********************************** */


    /*........................CANCEL ORDER VIEW (GET).......................*/

    @GetMapping("/cancel")
    public  String cancelOrder(
                               @RequestParam(name = "orderUuid") UUID orderUuid,
                               HttpSession session,Model model){
        session.setAttribute("orderUuid", orderUuid);
                     Optional<Order> orders=orderService.findOrderById(orderUuid) ;
                     Order order=orders.get();

        model.addAttribute("order",order);
        return "shop/cancel-order";
    }
    /* **********************************************  END CANCEL ORDER (GET) ********************************** */


    /*........................CANCEL ORDER VIEW (POST).......................*/
    @PostMapping("/cancel-order")
    public String cancelOrder(@ModelAttribute CancelReason orderCancelRequest,
                              HttpSession session,
                              @AuthenticationPrincipal(expression = "username") String username) {

        String cancelReq= String.valueOf(orderCancelRequest);

         UUID uuids = (UUID) session.getAttribute("orderUuid");
        orderService.cancel(uuids,cancelReq,username);
        Optional<Order> orders=orderService.findOrderById(uuids) ;
        Order order=orders.get();
        return "redirect:/order/myOrder";
    }
    /* **********************************************  END CANCEL ORDER (POST) ********************************** */


    /*........................RETURN ORDER VIEW (GET).......................*/
    @GetMapping("/return")
    public  String returnOrder(
            @RequestParam(name = "orderUuid") UUID orderUuid,
            HttpSession session,Model model){
        session.setAttribute("orderUuid", orderUuid);
        Optional<Order> orders=orderService.findOrderById(orderUuid) ;
        Order order=orders.get();

        model.addAttribute("order",order);
        return "shop/return-order";
    }
    /* **********************************************  END RETURN ORDER (GET) ********************************** */



    /*........................RETURN ORDER WITH DATE  (GET).......................*/


    @GetMapping("/home")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String showAllReturnOrder(Model model){
        return   orderReturned(1,model);
    }

    @GetMapping("/returnOrder/{pageNo}")
    public String orderReturned(@PathVariable(value = "pageNo") int pageNo,Model model){
            int pageSize = 4;
            Page<ReturnOrder> page = returnOrderService.findPaginated(pageNo, pageSize);
            List<ReturnOrder> returnOrders = page.getContent();

        List<List<ProductInfo>> productInfo = new ArrayList<>();
        List<List<OrderItems>> orderItems = new ArrayList<>();
        List<List<String>> productNames = new ArrayList<>();
        List<Double> totalPrices = new ArrayList<>();


        for (ReturnOrder returnOrder : returnOrders) {
            Order orderList = returnOrder.getOrder();
            List<ProductInfo> productInfos = new ArrayList<>();
            List<OrderItems> items = new ArrayList();
            List<String> names = new ArrayList();

            double totalPrice = 0.0;

            for (OrderItems orderItem : orderList.getOrderItems()) {
                ProductInfo productInf = orderItem.getProductInfo();
                productInfos.add(productInf);
                items.add(orderItem);
                names.add(productInf.getName());

                double itemPrice = orderItem.getPrice() * orderItem.getQuantity();
                totalPrice += itemPrice;
            }
            productNames.add(names);
            productInfo.add(productInfos);
            orderItems.add(items);
            totalPrices.add(totalPrice);

        }
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("totalPrices", totalPrices);
        model.addAttribute("productNames", productNames);
        model.addAttribute("orderItems", orderItems);
        model.addAttribute("productInfo", productInfo);
        model.addAttribute("returnOrders", returnOrders);
        return "admin/return-withdate";
    }
    /* **********************************************  END RETURN ORDER  ********************************** */




    /*........................RETURN ORDER  (POST).......................*/
    @PostMapping("/return-order")
    public String returnOrders(@ModelAttribute ReturnReason returnRequest,
                               HttpSession session,
                               @AuthenticationPrincipal(expression = "username") String username) {

        // Convert the enum to a string
        UUID uuids = (UUID) session.getAttribute("orderUuid");
        String returnReq= String.valueOf(returnRequest);


        orderService.returnOrder(uuids, returnReq, username);


        return "redirect:/order/myOrder";
    }

    /*Track order*/
    @GetMapping("/track-order")
    public String trackOrder(@RequestParam(name = "orderUuid") UUID orderUuid, Model model) {
        // Now you have access to the 'orderUuid' in your controller
        Optional<Order> order = orderService.findOrderById(orderUuid);
        if (order.isPresent()) {
            Order orderData = order.get();
            OrderStatus orderStatus = orderData.getOrderStatus();

            model.addAttribute("orderStatus", orderStatus);
            model.addAttribute("orderUuid", orderUuid);
        }
        return "shop/track-order";
    }


    /*............RETURN CAH IN WALLET RAZORPAY..................*/
    @GetMapping("/returnCashInWalletRazorPay")
    public String returnCashInWallet( @RequestParam(name = "orderUuid") UUID orderUuid,
                                      @AuthenticationPrincipal(expression = "username") String username, Model model) {

        Optional<Order> orders = orderService.findOrderById(orderUuid);
        Order returnOrder = orders.get();

        PaymentMode paymentMode=returnOrder.getPaymentMode();
        UserEntity user = userService.findByUsernames(username);
        System.out.println(paymentMode);
        PaymentMode paymentMode1 = returnOrder.getPaymentMode();
        String status = String.valueOf(returnOrder.getOrderStatus());

        String userReferralCode = user.getNewUserReferral();
        Wallet wallet = shopService.getWalletByUser(username);
        Double orderTotalPrice=0d;

        if (PaymentMode.RAZORPAY==(paymentMode)) {


            if(!returnOrder.isEnable()) {


                float earnedMoney = wallet.getEarnedMoney();
                float totalMoney = 0f;
                if (returnOrder != null) {


                    String orderStatus = String.valueOf(returnOrder.getOrderStatus());
                    orderTotalPrice = returnOrder.getTotalPrice();
                    totalMoney = earnedMoney += orderTotalPrice;
                    wallet.setTotalMoney(totalMoney);
                    returnOrder.setEnable(true);
                    shopService.saveWallet(wallet);
                }

            }
            model.addAttribute("orderTotalPrice",orderTotalPrice);
            model.addAttribute("userWallet", wallet);
            model.addAttribute("user", user);
            model.addAttribute("userReferralCode", userReferralCode);
            return "shop/wallet";
        }else {
            float totalMoney=wallet.getEarnedMoney();
             wallet.setTotalMoney(totalMoney);
             shopService.saveWallet(wallet);
            model.addAttribute("errorMessage", "Cash on Delivery refund is not applicable.");
            model.addAttribute("userWallet", wallet);
            model.addAttribute("user", user);
            model.addAttribute("userReferralCode", userReferralCode);
            return "shop/wallet";

        }

    }

    /* ********************************************** END  RETURN CAH IN WALLET RAZORPAY  ********************************** */



/*............INVOICE..................*/
    @GetMapping("/getinvoice")
    public ResponseEntity<byte[]> getInvoice(@RequestParam(name = "orderUuid") UUID orderUuid) throws Exception {

        Optional<Order> order =orderService.findOrderById(orderUuid);
        Order ordersInRange=order.get();
        byte[] reportBytes;
        String contentType;
        String filename;
        reportBytes = orderService.getOrderInvoice(ordersInRange);
        contentType = MediaType.APPLICATION_PDF_VALUE;
        filename = "Invoice.pdf";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentDispositionFormData("attachment", filename);

        return new ResponseEntity<>(reportBytes, headers, HttpStatus.OK);
    }


    /* *************************** END  INVOICE  ********************************** */



   /*.................REFERRAL DISCOUNT  (GET).................................*/
    @GetMapping("/referral-discount")
    public String applyReferralDiscount(@RequestParam("walletMoney") float walletMoney,
                                        @RequestParam("totalDiscountPrice") float totalDiscount, Model model) {

        float  totalDiscountPrice   = shopService.walletDiscount(walletMoney,totalDiscount);
        return "redirect:/cart/viewCart";
    }

    /* ************************************* END   REFERRAL DISCOUNT ********************************** */
}


