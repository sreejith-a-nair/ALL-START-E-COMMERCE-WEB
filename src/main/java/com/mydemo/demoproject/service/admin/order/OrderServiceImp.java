package com.mydemo.demoproject.service.admin.order;

import com.mydemo.demoproject.Entity.*;
import com.mydemo.demoproject.Entity.enumlist.CancelReason;
import com.mydemo.demoproject.Entity.enumlist.OrderStatus;
import com.mydemo.demoproject.Entity.enumlist.PaymentMode;
import com.mydemo.demoproject.Entity.enumlist.ReturnReason;
import com.mydemo.demoproject.Repository.admin.CouponRepo;
import com.mydemo.demoproject.Repository.admin.OrderItemRepo;
import com.mydemo.demoproject.Repository.admin.OrderRepo;
import com.mydemo.demoproject.Repository.admin.ProductRepo;
import com.mydemo.demoproject.Repository.shop.CancelOrderRepo;
import com.mydemo.demoproject.Repository.shop.CartRepo;
import com.mydemo.demoproject.Repository.shop.ReturnOrderRepo;
import com.mydemo.demoproject.service.admin.coupon.CouponService;
import com.mydemo.demoproject.service.shop.AddressService;
import com.mydemo.demoproject.service.shop.ShopService;
import com.mydemo.demoproject.service.user.UserService;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class OrderServiceImp implements OrderService {
    @Autowired
    AddressService addressService;

    @Autowired
    OrderItemRepo orderItemRepo;

    @Autowired
    CartRepo cartRepo;
    @Autowired
    UserService userService;

    @Autowired
    OrderRepo orderRepo;

    @Autowired
    ProductRepo productRepo;

    @Autowired
    CouponRepo couponRepo;

    @Autowired
    CouponService couponService;

    @Autowired
    CancelOrderRepo cancelOrderRepo;

    @Autowired
    ReturnOrderRepo returnOrderRepo;

    @Autowired
    ShopService shopService;

    private static final String KEY = "rzp_test_krZbRdGwanRtve";

    private static final String KEY_SECRET = "vPwSnArNx1RcrRK9z4KfmJi9";

    private static final String CURRENCY = "INR";


    @Override
    public Optional<Address> getAddressById(UUID selectedAddress) {
        if (selectedAddress != null) {
            System.out.println(selectedAddress);
            Optional<Address> userAddress = addressService.findAddressById(selectedAddress);
            System.out.println(userAddress);

            return userAddress;
        }
        return null;
    }

    @Override
    public Optional<UserEntity> getUserDataById(String username) {
        Optional<UserEntity> userEntitie = userService.getUserdata(username);
        return userEntitie;
    }

    @Override
    public List<Cart> getCartByUser(String username) {
        List<Cart> userCart = cartRepo.findByUserEntity_Username(username);
        return userCart;
    }

//    @Override
//    public List<Order> findOrderByUser(String username) {
//        List<Order> userOrders=orderRepo.findByUserEntity_Username(username);
//        System.out.println("<<<<<<<<<<<<<<<<<userOrders>>>>>>>>>>>>>>>>service>"+userOrders);
//        return  userOrders;
//    }

    @Override
    public Order saveOrder(Order order) {
        return orderRepo.save(order);
    }

    @Override
    public float findTotalUseQuantity(float quantity, float price) {
        float total = price * quantity;
        System.out.println("total in service>>>>>>" + total);
        return total;
    }


    @Override
    public List<Order> getAllOrders() {
        List<Order> orderList = orderRepo.findAll();
        System.out.println("admin side orderList" + orderList);
        return orderList;
    }

    /*new crete order*/
    @Override
    public Order createOrder(Address address, Double totalPrice, Double totalold, UserEntity user, PaymentMode paymentMode, List<Cart> cartlist, String username, List<Coupon> coupons) {


        List<Coupon> couponlist = couponService.findAll();
        if (address != null) {
            try {
                Order order = new Order();
                order.setPaymentMode(paymentMode);
                order.setOrderDate(LocalDateTime.now());
                if (paymentMode == PaymentMode.COD) {
                    order.setOrderStatus(OrderStatus.PENDING);
                } else if (paymentMode == PaymentMode.RAZORPAY) {
                    order.setOrderStatus(OrderStatus.PENDING);
                } else {
                    System.out.println("else case >>>>>>>>>>>>>>"+paymentMode);
                    order.setOrderStatus(OrderStatus.CONFIRMED);
                }
                order.setAddress(address);

                order.setUserEntity(user);

                /*CouponApplied price set */

                if (totalPrice.equals(totalold)) {
                    System.out.println("}OldddTotal++++$5$676re.....equal................................." + totalold);
                    order.setTotalPrice(totalold);
                } else {
                    System.out.println("}NewwwTotal++++$5$676re,not equal,,>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + totalPrice);
                    order.setTotalPrice(totalPrice);
                }
                /*end*/

                System.out.println("cart 1");
                /*     order Items*/
                List<OrderItems> orderItemsList = new ArrayList<>();

                System.out.println("cart 2");
                for (Cart cartItem : cartlist) {

                    OrderItems orderItem = new OrderItems();
                    orderItem.setPrice(cartItem.getProductInfo().getPrice()); // Set the price from the cart item
                    orderItem.setQuantity(cartItem.getQuantity()); // Set the quantity
                    orderItem.setProductInfo(cartItem.getProductInfo()); // Set the product info
                    orderItem.setOrder(order); // Set the order
                    orderItemsList.add(orderItem);
                }

                System.out.println("cart 3");
                order.setOrderItems(orderItemsList);
                System.out.println("cart 4");
                orderRepo.save(order);
                System.out.println("5");
                return order;

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("6");
            }

        }
        System.out.println("7 ");
        return null;
    }

    @Override
    public List<OrderItems> getCartItems() {
        List<OrderItems> orderItemsList = orderItemRepo.findAll();
        return orderItemsList;
    }

    @Override
    public Long updateStockOrder(Integer orderQuantity, UUID existProductUuid) {

        Optional<ProductInfo> existProduct = productRepo.findById(existProductUuid);
        Long existProductStock = existProduct.get().getStock();

        System.out.println("orderQuantity     .." + orderQuantity);
        System.out.println("existProductStock...." + existProductStock);

        Long newStock = existProductStock - orderQuantity;
        System.out.println("new stock in service update stock  =" + newStock);

        return newStock;
    }

    @Override
    public void removeProductFromCart(List<OrderItems> orderList, List<Cart> cartList) {
        try {
            for (Cart cartItem : cartList) {

                UUID cartuuid = cartItem.getProductInfo().getUuid();
                for (OrderItems orderItem : orderList) {

                    UUID productUuid = orderItem.getProductInfo().getUuid();


                    if (cartuuid.equals(productUuid)) {
                        System.out.println("cartuuid>>>>" + cartuuid);
                        cartRepo.delete(cartItem);
                    }

                }
            }

        } catch (Exception e) {
            System.out.println("error occurred>>>>>>>>>>>>>>>>when delete from cart");
        }

    }


    @Override
    public TransactionDetails createTransaction(Double amount) {
        try {
            // Construct the JSON request
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("amount", amount * 100);
//            jsonObject.put("currency", "INR");
            jsonObject.put("currency", CURRENCY);


            RazorpayClient razorpayClient = new RazorpayClient(KEY, KEY_SECRET);


            com.razorpay.Order order = razorpayClient.orders.create(jsonObject);


            TransactionDetails transactionDetails = prepareTransactionDetails(order);
            return transactionDetails;
        } catch (RazorpayException e) {

            e.printStackTrace();

        } catch (Exception e) {

            e.printStackTrace();

        }

        return null;
    }


    @Override
    public TransactionDetails prepareTransactionDetails(com.razorpay.Order order) {
        String orderId = order.get("id");
        String currency = order.get("currency");
        Integer amount = order.get("amount");

        TransactionDetails transactionDetails = new TransactionDetails(orderId, currency, amount, KEY);
        System.out.println("transactionDetails>>>>>>>>>in service..." + transactionDetails);
        return transactionDetails;
    }

    /*END*/

    @Override
    public Optional<Order> findOrderById(UUID savedOrderUuid) {
        return orderRepo.findById(savedOrderUuid);
    }

    @Override
    public List<Coupon> findCouponByCode(String couponCode) {
        List<Coupon> couponList = couponRepo.findByCode(couponCode);
        System.out.println("couponList>>>>>>>>>>>>>>>>>>>>" + couponList);
        return couponList;
    }

    @Override
    public float applyCouponFindTotal(List<Coupon> couponList, String username ,float totalDiscountPrices) {


        Optional<Integer> maxOffPercentage = couponList.stream()
                .map(Coupon::getOffPercentage)
                .max(Integer::compare);
        int percentage = maxOffPercentage.orElse(0);
        System.out.println("maxOffPercentage>>>>>>>>>>" + percentage);


        Optional<Integer> maxOfferPrice = couponList.stream()
                .map(Coupon::getMaxOff)
                .max(Integer::compare);
        int offerPrice = maxOfferPrice.orElse(0);
        System.out.println(">>>>>offerPrice>>>>>>" + offerPrice);

//
        List<Cart> cartlist = cartRepo.findByUserEntity_Username(username);

        /*new end*/

        Double total = 0d;
        for (Cart cart : cartlist) {
            total += cart.getQuantity() * cart.getProductInfo().getPrice();
        }
        System.out.println("fiind     total  " + total);

        if (total > 10000 || totalDiscountPrices > 10000) {
            System.out.println("inside if>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            int maxOffPercentageDiscount = (int) (percentage * totalDiscountPrices);
            int newMaxPercentage = maxOffPercentageDiscount / 100;

            System.out.println("newMaxPercentage=" + newMaxPercentage + "maximumMaxOff=" + offerPrice);

            if (offerPrice < newMaxPercentage) {

                float offerPriceApplied = (totalDiscountPrices - offerPrice);

                System.out.println("offerPriceApplied  service  offerPrice > max = " + offerPriceApplied);

                return offerPriceApplied;

            } else {

                float offerPercent = (totalDiscountPrices - newMaxPercentage);

                System.out.println("offerPriceApplied  service  = " + offerPercent);

                System.out.println("end if>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                return offerPercent;

            }

        } else {
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>total" + total);
            return totalDiscountPrices;
        }

    }

    @Override
    public Double applySingleCouponFindTotal(List<Coupon> couponList, String username, float oldTotalPrice) {
        Double oldTotal = (double) oldTotalPrice;
        Optional<Integer> maxOffPercentage = couponList.stream()
                .map(Coupon::getOffPercentage)
                .max(Integer::compare);
        int percentage = maxOffPercentage.orElse(0);
        System.out.println("maxOffPercentage>>>>>applySingleCouponFindTotal>>>>>" + percentage);


        Optional<Integer> maxOfferPrice = couponList.stream()
                .map(Coupon::getMaxOff)
                .max(Integer::compare);
        int offerPrice = maxOfferPrice.orElse(0);
        System.out.println(">>>>>offerPrice>>>>>>" + offerPrice);

        if (oldTotal > 10000) {
            int maxOffPercentageDiscount = (int) (percentage * oldTotal);
            int newMaxPercentage = maxOffPercentageDiscount / 100;

            System.out.println("newMaxPercentage single order=" + newMaxPercentage + "maximumMaxOff single order =" + offerPrice);

            if (offerPrice > newMaxPercentage) {
                Double offerPriceApplied = (double) (oldTotal - offerPrice);
                System.out.println("offerPriceApplied   = " + offerPriceApplied);

                return offerPriceApplied;

            } else {

                Double offerPercent = (double) (oldTotal - offerPrice);

                System.out.println("offerPriceApplied   = " + offerPercent);
                return offerPercent;

            }

        } else {
            System.out.println(">>>>>>>>>total" + oldTotal);
            return oldTotal;
        }

    }

    @Override
    public Page<Order> findPaginated(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        return this.orderRepo.findAll(pageable);
    }

    @Override
    public List<Order> searchOrderUserName(String keyword) {
        return orderRepo.findByUserNameKeyword(keyword);
    }

    @Override
    public List<Order> loadAllUser() {
        List<Order> orderList;
        orderList = orderRepo.findAll();
        return orderList;
    }

    @Override
    public List<Order> getOrderByUsername(String username) {
        List<Order> userOrders = orderRepo.findByUserEntity_Username(username);
        System.out.println(">>>>>>>>>>>>>>>>>>userOrders" + userOrders);
        return userOrders;
    }

    @Override
    public void cancel(UUID orderUuid, String cancelReason, String username) {


        Optional<UserEntity> userDetails = getUserDataById(username);
        UserEntity userEntity = userDetails.get();
        System.out.println("userDetails>>>>>>" + userDetails);

        Optional<Order> cancelOrder = orderRepo.findById(orderUuid);
        Order orderInfo = cancelOrder.get();
        System.out.println("Cancel order...." + orderInfo);

        CancelOrder cancelData = new CancelOrder();
        cancelData.setOrder(orderInfo);
        cancelData.setCancelReason(CancelReason.valueOf(cancelReason));
        cancelData.setCancellationDate(LocalDate.from(LocalDateTime.now()));
        cancelData.setUserEntity(userEntity);

        cancelOrderRepo.save(cancelData);
        orderInfo.setOrderStatus(OrderStatus.CANCELED);
        orderRepo.save(orderInfo);
        /*CANCEL ORDER STOCK UPDATE*/

        Order order = cancelData.getOrder();
        List<OrderItems> orderItems = order.getOrderItems();
        for (OrderItems orderProduct : orderItems) {

            Integer cancelledOrderQuantity = orderProduct.getQuantity();
            ProductInfo canceledProduct = orderProduct.getProductInfo();

            UUID uuid = canceledProduct.getUuid();
            Optional<ProductInfo> existProduct = productRepo.findById(uuid);
            ProductInfo existProductData = existProduct.get();

            if (existProductData.getUuid().equals(canceledProduct.getUuid())) {
//                Long cancelledProductQuantity= canceledProduct.getStock();
                Long existProductQuantity = existProductData.getStock();

                System.out.println("cancelledProductQuantity     " + cancelledOrderQuantity + "existProductQuantity>>>>>" + existProductQuantity);
                Long updateStock = cancelledOrderQuantity + existProductQuantity;

                System.out.println("UPDATED STOCK AFTER CANCEL " + updateStock);

                existProductData.setStock(updateStock);
                productRepo.save(existProductData);

            }

        }

    }

    @Override
    public void returnOrder(UUID orderUuid, String returnReason, String username) {


        Optional<UserEntity> userDetails = getUserDataById(username);
        UserEntity userEntity = userDetails.get();
        System.out.println("userDetails>>>>>>" + userDetails);

        Optional<Order> orderReturn = orderRepo.findById(orderUuid);
        Order orderInfo = orderReturn.get();
        System.out.println("Cancel order...." + orderInfo);

        ReturnOrder returnOrder = new ReturnOrder();
        returnOrder.setOrder(orderInfo);
        returnOrder.setReturnReason(ReturnReason.valueOf(returnReason));
        returnOrder.setCancellationDate(LocalDate.from(LocalDateTime.now()));
        returnOrder.setUserEntity(userEntity);

        returnOrderRepo.save(returnOrder);
        orderInfo.setOrderStatus(OrderStatus.RETURN);
        orderRepo.save(orderInfo);
        /*new update add returned product stock stock*/
        if (orderReturn.equals(ReturnReason.DAMAGED_PRODUCT)) {

        } else {
            Order order = returnOrder.getOrder();
            List<OrderItems> orderItems = order.getOrderItems();
            for (OrderItems orderProduct : orderItems) {

                Integer returnOrderQuantity = orderProduct.getQuantity();
                ProductInfo returnProduct = orderProduct.getProductInfo();
                UUID uuid = returnProduct.getUuid();
                Optional<ProductInfo> existProduct = productRepo.findById(uuid);
                ProductInfo existProductData = existProduct.get();

                if (existProductData.getUuid().equals(returnProduct.getUuid())) {
                    Long existProductQuantity = existProductData.getStock();
                    System.out.println("returnedProductQuantity     " + returnOrderQuantity + "existProductQuantity>>>>>" + existProductQuantity);
                    Long updateStock = returnOrderQuantity + existProductQuantity;
                    System.out.println("UPDATED STOCK AFTER RETURN   " + updateStock);
                    existProductData.setStock(updateStock);
                    productRepo.save(existProductData);

                }

            }


        }

    }

    @Override
    public byte[] getOrderInvoice(Order orders) throws Exception {
        double grandTotal = 0.0;
        System.out.println("grandTotal>>>>>>>>>>>>>>>>>>>>>>>>");

        Address userAddress = orders.getAddress();

        String sales = "<html><body>";
        sales += "<h2> ALL-STAR Pvt .Ltd </h2>";
        sales += "<h3>ORDER INVOICE</h3>";

        // Add user address details to the invoice
        if (userAddress != null) {
            sales += "<h4>User Address</h4>";
            sales += "<h5>" + userAddress.getHouseNumberOrName()+", "+userAddress.getLandmark()+"</h5>";
            sales += "<p>" + userAddress.getArea() + "</p>";
            sales += "<p>" + userAddress.getCity() + ", " +userAddress.getDistrict()+", "+ userAddress.getState() + " " + userAddress.getPincode() + "</p>";
        }


        sales += "<h2> INVOICE DETAILS </h2>";
        sales += "<h4>Product Details</h4>";
        sales += "<table width='100%' border='1' cellpadding='8'>";
        sales += "<tr>";
        sales += "<th style='background-color: #f2f2f2; text-align: left;'>User</th>";
        sales += "<th style='background-color: #f2f2f2; text-align: left;'>Product</th>";
        sales += "<th style='background-color: #f2f2f2; text-align: left;'>Quantity</th>";
        sales += "<th style='background-color: #f2f2f2; text-align: left;'>Payment Mode</th>";
        sales += "<th style='background-color: #f2f2f2; text-align: left;'>Price</th>";
        sales += "<th style='background-color: #f2f2f2; text-align: left;'>Date</th>";
        sales += "</tr>";


        List<OrderItems> orderItems = orders.getOrderItems();

        for (OrderItems orderItems1 : orderItems) {

                sales += "<tr>";

                sales += "<td>" + orders.getUserEntity().getFirstname() + "</td>";

                sales += "<td>" + orderItems1.getProductInfo().getName() + "</td>";

                sales += "<td>" + orderItems1.getQuantity() + "</td>";

                sales += "<td>" + orders.getPaymentMode() + "</td>";

                sales += "<td>" + orderItems1.getPrice() + "</td>";

                sales += "<td>" + orders.getOrderDate() + "</td>";

                sales += "</tr>";

        }

        sales += "</table>";
//        sales += "<h5>Total sales</h5>" + orders.getTotalPrice();
        sales += "<h3>"+"Total sales" + " : "+orders.getTotalPrice()+"</h3>";
        sales += "</body></html>";

        ITextRenderer renderer = new ITextRenderer();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        renderer.setDocumentFromString(sales);
        renderer.layout();
        renderer.createPDF(outputStream);

        return outputStream.toByteArray();
    }

    @Override
    public float walletPay(Double total, float walletMoney) {
        if (total <= walletMoney) {

            float walletPay = (float) (walletMoney-total);
            return  walletPay;
        } else {
            double newTotal =total;
            float totalAmount= (float) newTotal;
            System.out.println("totalll else>>>>>service>>>>>>>> "+totalAmount);
            return totalAmount;
        }


    }
/*Update wallet */
    @Override
    public void updateWallet(float newWalletPrice, String username,Double totalPrice) {
        System.out.println("Wallet total usernmae>>>actual total of order>>>>>>>>"+newWalletPrice + " "  +username + "  "+totalPrice);

        Wallet wallet=shopService.getWalletByUser(username);
        float walletTotal= wallet.getTotalMoney();

        if(walletTotal!=newWalletPrice&&totalPrice!=newWalletPrice){
        System.out.println("Wallet total update>>>>>>>>>>>>>>>>>"+newWalletPrice);

        wallet.setTotalMoney(newWalletPrice);
        shopService.saveWallet(wallet);
        }
        else {
            wallet.setTotalMoney(walletTotal);
            shopService.saveWallet(wallet);
        }
    }


}

