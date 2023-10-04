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
import com.mydemo.demoproject.service.admin.coupon.CouponService;
import com.mydemo.demoproject.service.admin.order.OrderService;
import com.mydemo.demoproject.service.admin.product.ProductService;
import com.mydemo.demoproject.service.shop.AddressService;
import com.mydemo.demoproject.service.shop.CartService;
import com.mydemo.demoproject.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
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
    CancelOrderRepo cancelOrderRepo;

    @Autowired
    OrderItemRepo orderItemsRepo;


    /*Order show*/
    @GetMapping("/show/{uuid}")
    public String showOrder(@PathVariable UUID uuid, Model model,
                            @AuthenticationPrincipal(expression = "username") String username) {

        Optional<UserEntity> user = userService.getUserdata(username);
        List<Address> useraddresses = user.map(UserEntity::getAddresses).orElse(Collections.emptyList());
        List<Address> enabledAddresses = useraddresses.stream()
                .filter(Address::isEnabled)
                .collect(Collectors.toList());
        List<Cart> cartList = cartService.findByUserEntity_Usernames(username);
        System.out.println("cartList>>>>>>>>>>>>>>>" + useraddresses);

        List<Coupon> couponList = couponService.findAll();
        System.out.println(">>>..couponList" + couponList);
        Double totalprice= (double) cartService.findTotal(cartList);
        boolean available;
        if(totalprice>10000){available=true;
            System.out.println("true..............."+totalprice);
        }
        else{available=false;
            System.out.println("fale................."+totalprice);
        }
//        if()
        model.addAttribute("available",available);
        model.addAttribute("couponList", couponList);
        model.addAttribute("total",totalprice );
        model.addAttribute("user", user);
        model.addAttribute("useraddresses", enabledAddresses);
        model.addAttribute("cartList", cartList);
        return "shop/order";
    }


    /*Buy single product*/
    @GetMapping("/show-single-product/{productId}/{quantity}")
    public String showSingleOrder(
            @PathVariable("productId") UUID productId,
            @PathVariable("quantity") float quantity,
            Model model,
            @AuthenticationPrincipal(expression = "username") String username,
            HttpSession session) {

        System.out.println(">>>>>>>>>>>>>>>>>>>>productuuid" + productId);

        /*ADDRESS FILTERING*/
        Optional<UserEntity> user = userService.getUserdata(username);
        List<Address> userAddresses = user.map(UserEntity::getAddresses).orElse(Collections.emptyList());
        List<Address> enabledAddresses = userAddresses.stream()
                .filter(Address::isEnabled)
                .collect(Collectors.toList());


        Optional<ProductInfo> product = productService.getProduct(productId);


        Double totalprice = (double) 0;
        if (product.isPresent()) {
            ProductInfo productPrice = product.get();
            long price = (long) productPrice.getPrice();
            System.out.println("Product Price: " + price);
            totalprice = (double) orderService.findTotalUseQuantity(price, quantity);

        } else {

            System.out.println("Product not found.");
        }


        List<Cart> cartList = orderService.getCartByUser(username);


        List<Coupon> couponList = couponService.findAll();
    session.setAttribute("productUUID", productId);
    session.setAttribute("productQuantity",quantity);
    session.setAttribute("user", user);
    /*new*/
    boolean available;
    if(totalprice>10000){available=true;
        System.out.println("true..............."+totalprice);
         }
    else{available=false;
        System.out.println("fale................."+totalprice);
       }
        model.addAttribute("available",available);
        model.addAttribute("couponList", couponList);
        model.addAttribute("quantity" + quantity);
        model.addAttribute("total", totalprice);
        model.addAttribute("user", user);
        model.addAttribute("userAddresses", enabledAddresses);
        model.addAttribute("product", product.orElse(null));

        return "shop/single-order";
    }


    /*Apply single order coupon*/
    @PostMapping("/applySingleOrderCoupon")
    /*End  Apply single order coupon*/
    public String applySingleOrderCoupon(@RequestParam(name = "couponCode") String couponCode,
                              @AuthenticationPrincipal(expression = "username") String username,
                              Model model, HttpSession session) {

        session.setAttribute("couponCode", couponCode);

        Optional<UUID> productUUIDOptional = Optional.ofNullable((UUID) session.getAttribute("productUUID"));

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

        Double totalprice = orderService.applySingleCouponFindTotal(coupons, username, oldtotal);

        boolean available;
        if(totalprice>10000){available=true;

        }
        else{available=false;

        }

            ArrayList allCoupon = new ArrayList();

            model.addAttribute("available",available);
            model.addAttribute("couponApplied", true);
            model.addAttribute("quantity" + quantity);
            model.addAttribute("total", totalprice);
            model.addAttribute("user", user);
            model.addAttribute("userAddresses", enabledAddresses);
            model.addAttribute("product", product.orElse(null));

            return "shop/single-order";
//        }
    }


    /*Check outCoupon*/
    @PostMapping("/applyCoupon")
    public String applyCoupon(@RequestParam(name = "couponCode") String couponCode,
                              @AuthenticationPrincipal(expression = "username") String username,
                              Model model, HttpSession session) {

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
        System.out.println("existTotal" + existTotal);


        Double total = orderService.applyCouponFindTotal(coupons, username);

        boolean available;
        if(total>10000){available=true;
            System.out.println("true.....aply checkout.........."+total);
        }
        else{available=false;
            System.out.println("fale..aply...ckeckout............"+total);
        }




        System.out.println("product uuid is not present");
            ArrayList couponList = new ArrayList();

            System.out.println("newTotal==+" + total);
            model.addAttribute("available",available);
            model.addAttribute("couponApplied", true);
            model.addAttribute("couponList", couponList);
            model.addAttribute("user", user);
            model.addAttribute("total", total);
            model.addAttribute("useraddresses", enabledAddresses);
            model.addAttribute("cartList", cartList);
            return "shop/order";
//        }
    }


    /*Post order*/
    @PostMapping("/submit-selected-address-and-payment")
    public String submitOrder(@RequestParam("selectedAddress") UUID selectedAddress,
                              @RequestParam("paymentMethod") PaymentMode selectedPaymentOption,
                              @RequestParam("total") Double total, // Adjust the type if necessary
                              @AuthenticationPrincipal(expression = "username") String username,
                              Model model, HttpSession session) {

        String couponCode = (String) session.getAttribute("couponCode");

        List<Coupon> coupons = orderService.findCouponByCode(couponCode);

        Optional<UserEntity> userDetails = orderService.getUserDataById(username);

        UserEntity userEntity = userDetails.get();

        List<Cart> cartList = orderService.getCartByUser(username);

        Double totalPrice = cartList.stream().mapToDouble(
                cartItems -> cartItems.getProductInfo().getPrice() * cartItems.getQuantity()).sum();

        Optional<Address> address = orderService.getAddressById(selectedAddress);

        Address userAddresses = address.get();


        /*RAZORPAY*/

        if (selectedPaymentOption == PaymentMode.RAZORPAY) {

            System.out.println("razor pay only");

            if (total.equals(totalPrice)) {
                TransactionDetails transactionDetails = createTransaction(totalPrice);
                System.out.println("Total>>>>>>   transaction  " + totalPrice);

                if (userAddresses==null) {

                    model.addAttribute("error", "Please add an address before proceeding.");
                    return "redirect:/addresses";
                }

                Order createOrder = orderService.createOrder(userAddresses, total,totalPrice, userEntity, selectedPaymentOption, cartList, username,coupons);

                UUID savedUuid = createOrder.getUuid();
                Optional<Order> order = orderService.findOrderById(savedUuid);
                Order recentOrder = order.get();
                List<OrderItems> orderItem = recentOrder.getOrderItems();


//                orderService.stockManage(orderItemsList, productInfo);

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
                System.out.println(   "newTotal>>>>>>> price not equal offer price transaction "+total);

                if (userAddresses==null) {

                    model.addAttribute("error", "Please add an address before proceeding.");
                    return "redirect:/addresses"; // Redirect to the address management page or a custom error page
                }

                Order createOrder = orderService.createOrder(userAddresses, total,totalPrice, userEntity, selectedPaymentOption, cartList, username,coupons);

                UUID savedUuid = createOrder.getUuid();
                Optional<Order> order = orderService.findOrderById(savedUuid);
                Order recentOrder = order.get();
                List<OrderItems> orderItem = recentOrder.getOrderItems();

//                orderService.stockManage(orderItemsList, productInfo);

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

        /* Create order */
        Order createOrder = orderService.createOrder(userAddresses, total,totalPrice, userEntity, selectedPaymentOption, cartList, username,coupons);

        Order saveOrder = orderService.saveOrder(createOrder);
        UUID savedOrderUuid = saveOrder.getUuid();

        Optional<Order> order = orderService.findOrderById(savedOrderUuid);
        Order recentOrder = order.get();
        List<OrderItems> orderItems = recentOrder.getOrderItems();

        /*new update*/

        for (OrderItems orderItem : orderItems) {
            ProductInfo productData = orderItem.getProductInfo();

            System.out.println("new update in stock");

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

//        orderService.stockManage(orderItemsList, productInfo);

        orderService.removeProductFromCart(orderItems, cartList);


        model.addAttribute("username", username);
        return "shop/order-confirmation";

    }

    /*MyOrder Show in user side */

    @GetMapping("/myOrder")
    public String  myOrder( @AuthenticationPrincipal(expression = "username") String username,
                            Model model){
        System.out.println("USERName>>>>"+username);

        List<Order>myOrders=orderService.getOrderByUsername(username);
        System.out.println("My Order>?????  "+myOrders);
        String currentOrderStatus = myOrders.stream()
                .findFirst()
                .map(order -> order.getOrderStatus().toString())
                .orElse("No orders found"); //
        System.out.println(" currentOrderStatus   =  "+currentOrderStatus);

        model.addAttribute("myOrders",myOrders);
   return  "shop/my-order";
    }


    /*Update order status*/

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


    /*end Update order status*/


    /*pagination*/
    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String showAllOrder(Model model) {
        return findPaginated(1, model);
    }


    /*pagination*/

    @GetMapping("/page/{pageNo}")
    public String findPaginated(@PathVariable(value = "pageNo") int pageNo, Model model) {
        int pageSize = 5;
        Page<Order> page = orderService.findPaginated(pageNo, pageSize);
        List<Order> orders = page.getContent();
        System.out.println("orders in pagination" + orders);
        System.out.println("page in pagination" + page);

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("orders", orders);

        return "admin/order-list";
    }



    /*pagination end*/

    /*View order pagination*/
//    @GetMapping("/viewOrder")
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
//    public String viewOrderDetails(Model model){
//        return   findPaginate(1,model);
//    }


    /*pagination*/

    @GetMapping("/pages/{uuid}")
    public String findPaginate(@PathVariable("uuid") UUID uuid, Model model) {
//        int pageSize=5;
//        Page<Order> page=orderService.findPaginated(pageNo,pageSize);
//        List<Order>orders=page.getContent();
        Optional<Order> order = orderService.findOrderById(uuid);


        List<OrderItems> orderItems = order.get().getOrderItems();
        System.out.println(">>>orderItems" + orderItems);
//            String name=orderItems.get().getProductInfo().getName();

//        System.out.println("orders in pagination"+orders);
//        System.out.println("page in pagination"+page);

//        model.addAttribute("currentPage", pageNo);
//        model.addAttribute("totalPages", page.getTotalPages());
//        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("orderItems", orderItems);
        model.addAttribute("order", order.get());

        return "admin/order-detail";
    }


    /*end View order pagination*/


    /*Razorpay Transaction*/
    @PostMapping("/createTransaction/{amount}")
    @PreAuthorize("hasRole('Admin')")
    public TransactionDetails createTransaction(@PathVariable(name = "amount") Double amount) {
        return orderService.createTransaction(amount);

    }


    //  Razorpay payment success
    @GetMapping("/payment/success")
    public String handlePaymentSuccess() {
        System.out.println("razorr oredr  pay suuces");

        return "shop/success";
    }



    /*search*/
    @GetMapping(value = "/search", params = "keyword")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String searchOrderUser(Model model, @Param("keyword") String keyword) {
//        System.out.println("search>begin.....>>>>>>>");
        try {
            List<Order> orders;

            if (keyword != null && !keyword.isEmpty()) {
                orders = orderService.searchOrderUserName(keyword);
                System.out.println("search>>>>if" + orders);

            } else {
                //System.out.println("search--else------->>>");

                orders = orderService.loadAllUser();
            }
            System.out.println("orderList>>>>>>>" + orders);

            model.addAttribute("orders", orders);

            return "admin/order-list";

        } catch (Exception e) {

            e.printStackTrace();

            model.addAttribute("errorMessage", "An error occurred while processing your request.");
            return "error";
        }

    }



    /*address add and update */


    /*Update address post*/
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

    /*Cancel order <<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>*/

    @GetMapping("/cancel")
    public  String cancelOrder(
                               @RequestParam(name = "orderUuid") UUID orderUuid,
                               HttpSession session,Model model){
        session.setAttribute("orderUuid", orderUuid);
        System.out.println("UUUUUUid  "+orderUuid);
                     Optional<Order> orders=orderService.findOrderById(orderUuid) ;
                     Order order=orders.get();

        model.addAttribute("order",order);
        return "shop/cancel-order";
    }


    @PostMapping("/cancel-order")
    public String cancelOrder(@ModelAttribute CancelReason orderCancelRequest,
                              HttpSession session,
                              @AuthenticationPrincipal(expression = "username") String username) {

        System.out.println("cancel object orderCancelRequest"+orderCancelRequest);
        String cancelReq= String.valueOf(orderCancelRequest);

         UUID uuids = (UUID) session.getAttribute("orderUuid");
        System.out.println("Order UUID: " + uuids);
        orderService.cancel(uuids,cancelReq,username);
        Optional<Order> orders=orderService.findOrderById(uuids) ;
        Order order=orders.get();
        return "redirect:/order/myOrder";
    }



    /*return order*/
    @GetMapping("/return")
    public  String returnOrder(
            @RequestParam(name = "orderUuid") UUID orderUuid,
            HttpSession session,Model model){
        session.setAttribute("orderUuid", orderUuid);
        System.out.println("UUUUUUid  "+orderUuid);
        Optional<Order> orders=orderService.findOrderById(orderUuid) ;
        Order order=orders.get();

        model.addAttribute("order",order);
        return "shop/return-order";
    }

    @PostMapping("/return-order")
    public String returnOrders(@ModelAttribute ReturnReason returnRequest,
                               HttpSession session,
                               @AuthenticationPrincipal(expression = "username") String username) {

        // Convert the enum to a string
        UUID uuids = (UUID) session.getAttribute("orderUuid");
        String returnReq= String.valueOf(returnRequest);
        System.out.println("Order UUID: " + uuids);
        System.out.println("Return Reason: " + returnReq);

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

//    @GetMapping("/returnOrder")
//    public String orderReturned(Model model) {
//        List<ReturnOrder> returnOrders = returnOrderRepo.findAll();
//        List<ProductInfo> productInfo = new ArrayList<>();
//        List<OrderItems> orderItems = new ArrayList<>();
//
//        for (ReturnOrder returnOrder : returnOrders) {
//            Order orderList = returnOrder.getOrder();
//            for (OrderItems orderItem : orderList.getOrderItems()) {
//                ProductInfo productInfos = orderItem.getProductInfo();
//                productInfo.add(productInfos);
//                orderItems.add(orderItem);
//            }
//        }
//        model.addAttribute("orderItems",orderItems);
//        model.addAttribute("productInfo", productInfo);
//        model.addAttribute("returnOrders", returnOrders);
//        return "admin/return-withdate";
//    }

    @GetMapping("/returnOrder")
    public String orderReturned(Model model) {
        List<ReturnOrder> returnOrders = returnOrderRepo.findAll();
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

        model.addAttribute("totalPrices", totalPrices);
        model.addAttribute("productNames", productNames);
        model.addAttribute("orderItems", orderItems);
        model.addAttribute("productInfo", productInfo);
        model.addAttribute("returnOrders", returnOrders);
        return "admin/return-withdate";
    }




}







