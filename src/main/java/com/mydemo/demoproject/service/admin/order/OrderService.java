package com.mydemo.demoproject.service.admin.order;


import com.mydemo.demoproject.Entity.*;
import com.mydemo.demoproject.Entity.enumlist.PaymentMode;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderService {

    Optional<Address> getAddressById(UUID selectedAddress);

    Optional<UserEntity> getUserDataById(String username);

    List<Cart>getCartByUser(String username);



    Order saveOrder(Order order);

//    find single product total
  float findTotalUseQuantity(long quantity,float price);

   List<Order>getAllOrders();


   /*Create order*/
    Order createOrder(Address address, Double totalPrice,Double totalold, UserEntity user, PaymentMode paymentMode,List<Cart>cartlist,String username,List<Coupon>couponList);

    /*Create Transaction*/
    TransactionDetails createTransaction(Double amount);

     TransactionDetails prepareTransactionDetails(com.razorpay.Order order);


    /*trtansacton end*/

    /*get cartItem after save*/
  List<OrderItems> getCartItems();


  Long updateStockOrder(Integer orderProductQuantity, UUID existProductUuid) ;

/* After order remove product From cart*/
void removeProductFromCart(List<OrderItems> orderList,List<Cart> cartList);



/*find order by id*/
Optional<Order> findOrderById(UUID savedOrderUuid);



   List<Coupon> findCouponByCode(String couponCode);
/*find order by user*/
//List<Order> findOrderByUser(String username);



    Double applyCouponFindTotal(List<Coupon>couponList,String username);

//    public Double applyCouponToOrder(Coupon coupon, float product) ;
     Double applySingleCouponFindTotal(List<Coupon>couponList,String username,float oldtotal);

    /*pagination*/

    Page<Order> findPaginated(int pageNo, int pageSize);


    /*end pagination*/

    /* Search product method*/
    List<Order> searchOrderUserName(String keyword);

    public List<Order> loadAllUser();


    /*My order*/
    List<Order> getOrderByUsername( String username);

    /*Get order items in One order*/
//     List<OrderItems> getOrderItemsByOrder(Order order);

/*cancel order*/
 void cancel(UUID orderUuid,String cancelReason,String username);

    void returnOrder(UUID orderUuid, String cancelReason,String  username);


    /*update stock*/
//    void updateStock(Order order);
}
