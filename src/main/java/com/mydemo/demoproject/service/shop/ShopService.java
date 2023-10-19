package com.mydemo.demoproject.service.shop;

import com.mydemo.demoproject.Entity.ProductInfo;
import com.mydemo.demoproject.Entity.ReturnOrder;
import com.mydemo.demoproject.Entity.Wallet;

import java.util.List;

public interface ShopService {

//    List<ProductInfo> searchProductName(String keyword);


    List<ProductInfo> loadAllProduct() ;
    String getReferralCode(String username);

    void addMoney(String user,String referralCode);

    void  saveWallet(Wallet wallet);

    Wallet getWalletByUser(String username);

    float getMoney(String username);

    float walletDiscount(float walletMoney,float totalDiscountPrice);

//    Double addReturnMoneyInwallet(float earnedMoney, List<ReturnOrder> returnOrderList,Wallet wallet,String orderStatus);
//
public  void sendMail(String toEmail,
                      String subject,
                      String body);

    boolean validateEmail(String email);
}
