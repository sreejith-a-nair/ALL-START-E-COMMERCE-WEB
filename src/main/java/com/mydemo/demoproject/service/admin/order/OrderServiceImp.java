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


    @Override
    public Order saveOrder(Order order) {
        return orderRepo.save(order);
    }

    @Override
    public float findTotalUseQuantity(float quantity, float price) {
        float total = price * quantity;
        return total;
    }


    @Override
    public List<Order> getAllOrders() {
        List<Order> orderList = orderRepo.findAll();
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
                    order.setOrderStatus(OrderStatus.CONFIRMED);
                }
                order.setAddress(address);

                order.setUserEntity(user);

                /*CouponApplied price set */

                if (totalPrice.equals(totalold)) {
                    order.setTotalPrice(totalold);
                } else {
                    order.setTotalPrice(totalPrice);
                }
                /*end*/


                /*     order Items*/
                List<OrderItems> orderItemsList = new ArrayList<>();

                for (Cart cartItem : cartlist) {

                    OrderItems orderItem = new OrderItems();
                    orderItem.setPrice(cartItem.getProductInfo().getPrice()); // Set the price from the cart item
                    orderItem.setQuantity(cartItem.getQuantity()); // Set the quantity
                    orderItem.setProductInfo(cartItem.getProductInfo()); // Set the product info
                    orderItem.setOrder(order); // Set the order
                    orderItemsList.add(orderItem);
                }


                order.setOrderItems(orderItemsList);

                orderRepo.save(order);

                return order;

            } catch (Exception e) {
                e.printStackTrace();

            }

        }

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


        Long newStock = existProductStock - orderQuantity;


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

                        cartRepo.delete(cartItem);
                    }

                }
            }

        } catch (Exception e) {

        }

    }


    @Override
    public TransactionDetails createTransaction(Double amount) {
        try {
            int amountRazorpay = (int) (amount * 100);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("amount", amountRazorpay);

            jsonObject.put("currency", CURRENCY);


            RazorpayClient razorpayClient = new RazorpayClient(KEY, KEY_SECRET);


            com.razorpay.Order order = razorpayClient.orders.create(jsonObject);


            TransactionDetails transactionDetails = prepareTransactionDetails(order);
            return transactionDetails;
        } catch (RazorpayException e) {

            e.printStackTrace();
            System.err.println("Razorpay error: " + e.getMessage());

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
        return couponList;
    }

    @Override
    public float applyCouponFindTotal(List<Coupon> couponList, String username ,float totalDiscountPrices) {


        Optional<Integer> maxOffPercentage = couponList.stream()
                .map(Coupon::getOffPercentage)
                .max(Integer::compare);
        int percentage = maxOffPercentage.orElse(0);


        Optional<Integer> maxOfferPrice = couponList.stream()
                .map(Coupon::getMaxOff)
                .max(Integer::compare);
        int offerPrice = maxOfferPrice.orElse(0);

        List<Cart> cartlist = cartRepo.findByUserEntity_Username(username);

        /*new end*/

        Double total = 0d;
        for (Cart cart : cartlist) {
            total += cart.getQuantity() * cart.getProductInfo().getPrice();
        }


        if (total > 10000 || totalDiscountPrices > 10000) {
            int maxOffPercentageDiscount = (int) (percentage * totalDiscountPrices);
            int newMaxPercentage = maxOffPercentageDiscount / 100;


            if (offerPrice < newMaxPercentage) {

                float offerPriceApplied = (totalDiscountPrices - offerPrice);


                return offerPriceApplied;

            } else {

                float offerPercent = (totalDiscountPrices - newMaxPercentage);

                return offerPercent;

            }

        } else {
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


        Optional<Integer> maxOfferPrice = couponList.stream()
                .map(Coupon::getMaxOff)
                .max(Integer::compare);
        int offerPrice = maxOfferPrice.orElse(0);

        if (oldTotal > 10000) {
            int maxOffPercentageDiscount = (int) (percentage * oldTotal);
            int newMaxPercentage = maxOffPercentageDiscount / 100;

            if (offerPrice > newMaxPercentage) {
                Double offerPriceApplied = (double) (oldTotal - offerPrice);

                return offerPriceApplied;

            } else {

                Double offerPercent = (double) (oldTotal - offerPrice);
                return offerPercent;

            }

        } else {
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
        return userOrders;
    }

    @Override
    public void cancel(UUID orderUuid, String cancelReason, String username) {


        Optional<UserEntity> userDetails = getUserDataById(username);
        UserEntity userEntity = userDetails.get();

        Optional<Order> cancelOrder = orderRepo.findById(orderUuid);
        Order orderInfo = cancelOrder.get();

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
                Long updateStock = cancelledOrderQuantity + existProductQuantity;


                existProductData.setStock(updateStock);
                productRepo.save(existProductData);

            }

        }

    }

    @Override
    public void returnOrder(UUID orderUuid, String returnReason, String username) {


        Optional<UserEntity> userDetails = getUserDataById(username);
        UserEntity userEntity = userDetails.get();

        Optional<Order> orderReturn = orderRepo.findById(orderUuid);
        Order orderInfo = orderReturn.get();

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
                    Long updateStock = returnOrderQuantity + existProductQuantity;
                    existProductData.setStock(updateStock);
                    productRepo.save(existProductData);

                }

            }


        }

    }

    @Override
    public byte[] getOrderInvoice(Order orders) throws Exception {
        double grandTotal = 0.0;

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

        Wallet wallet=shopService.getWalletByUser(username);
        float walletTotal= wallet.getTotalMoney();

        if(walletTotal!=newWalletPrice&&totalPrice!=newWalletPrice){

        wallet.setTotalMoney(newWalletPrice);
        shopService.saveWallet(wallet);
        }
        else {
            wallet.setTotalMoney(walletTotal);
            shopService.saveWallet(wallet);
        }
    }



    //    new update
    @Override
    public int totalOrders() {
        List<Order> allOrder= orderRepo.findAll();
        int totalOrderCount=0;
        for (Order totalOrder:allOrder){
            System.out.println(totalOrder.getTotalPrice());
            totalOrderCount++;
        }
        return totalOrderCount;
    }

    @Override
    public Long totalOrderPrice() {
        List<Order> allOrder= orderRepo.findAll();
        Long totalOrderPrice=0l;

        for (Order totalOrder:allOrder){
            System.out.println(totalOrder.getTotalPrice());
            totalOrderPrice = (long) (totalOrderPrice+totalOrder.getTotalPrice());
        }
        return totalOrderPrice;
    }



    /*New*/

    @Override
    public int getDailyTotalSale(LocalDateTime startDate, LocalDateTime endDate) {
        List<Order> dailyOrder=orderRepo.findByOrderDateBetween(startDate, endDate);
        int dailyTotalPrice=0;
        for (Order orders:dailyOrder){
            dailyTotalPrice  = (int) (dailyTotalPrice+orders.getTotalPrice());

        }
        return dailyTotalPrice;
    }

    @Override
    public int getMonthlyTotalSale(LocalDateTime startDate, LocalDateTime endDate) {
        List<Order> monthlyOrder=orderRepo.findByOrderDateBetween(startDate, endDate);
        int monthlyTotalPrice=0;
        for (Order orders:monthlyOrder){
            monthlyTotalPrice  = (int) (monthlyTotalPrice+orders.getTotalPrice());

        }
        return monthlyTotalPrice;
    }

    @Override
    public int getWeeklyTotalSale(LocalDateTime startDate, LocalDateTime endDate) {
        List<Order> weeklyOrder=orderRepo.findByOrderDateBetween(startDate, endDate);
        int weeklyTotalPrice=0;
        for (Order orders:weeklyOrder){
            weeklyTotalPrice = (int) (weeklyTotalPrice+orders.getTotalPrice());

        }
        return weeklyTotalPrice;
    }

    @Override
    public int getDeliveredCount() {
        List<Order>orders=orderRepo.findAll();
        int count=0;
        for (Order orderList:orders){

            if(orderList.getOrderStatus().equals(OrderStatus.DELIVERED)){
                count++;
            }
        }
        return count;
    }

    @Override
    public int getCanceledCount() {
        List<Order>orders=orderRepo.findAll();
        int count=0;
        for (Order orderList:orders){

            if(orderList.getOrderStatus().equals(OrderStatus.CANCELED)){
                count++;
            }
        }
        return count;
    }

    @Override
    public int getShippedCount() {
        List<Order>orders=orderRepo.findAll();
        int count=0;
        for (Order orderList:orders){

            if(orderList.getOrderStatus().equals(OrderStatus.SHIPPED)){
                count++;
            }
        }
        return  count;
    }

    @Override
    public int getReturnedCount() {
        List<Order>orders=orderRepo.findAll();
        int count=0;
        for (Order orderList:orders){

            if(orderList.getOrderStatus().equals(OrderStatus.RETURN)){
                count++;
            }
        }
        return  count;
    }

}

