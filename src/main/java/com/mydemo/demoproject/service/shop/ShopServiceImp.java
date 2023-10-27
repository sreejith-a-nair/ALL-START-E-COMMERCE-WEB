package com.mydemo.demoproject.service.shop;

import com.mydemo.demoproject.Entity.ProductInfo;

import com.mydemo.demoproject.Entity.ReturnOrder;
import com.mydemo.demoproject.Entity.UserEntity;
import com.mydemo.demoproject.Entity.Wallet;
import com.mydemo.demoproject.Repository.admin.ProductRepo;

import com.mydemo.demoproject.Repository.shop.WalletRepo;
import com.mydemo.demoproject.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class ShopServiceImp implements  ShopService{
    @Autowired
    ProductRepo productRepo;

    @Autowired
    UserService userService;

    @Autowired
    WalletRepo walletRepo;

    @Autowired
    private JavaMailSender mailSender;



    @Override
    public List<ProductInfo> loadAllProduct() {
        List  <ProductInfo> productList;
        productList = productRepo.findAll();
        return productList;
    }

    @Override
    public String getReferralCode(String username) {

        UUID uuid = UUID.randomUUID();
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        int codeLength = 8;
        SecureRandom random = new SecureRandom();
        char[] codeChars = new char[codeLength];
        for (int i = 0; i < codeLength; i++) {
            int randomIndex = random.nextInt(characters.length());
            codeChars[i] = characters.charAt(randomIndex);
        }

        String uniqueCode = new String(codeChars);

        System.out.println("Referral Code: " + uniqueCode);

        return uniqueCode;

    }

    @Override
    public void addMoney(String user,String referralCode) {
        if(!user.isEmpty()) {
            UserEntity userEntity =userService.findByUsernames(user);
            Wallet userWallet = new Wallet();
            float referralMoney=100f;
             userWallet.setUserEntity(userEntity);
             userWallet.setEarnedMoney(referralMoney);
            walletRepo.save(userWallet);
        }
    }

    @Override
    public void saveWallet(Wallet wallet) {
        walletRepo.save(wallet);
    }



    @Override
    public Wallet getWalletByUser(String username) {
        Wallet walletData= walletRepo.findByUserEntity_Username(username);
        return walletData;
    }

    @Override
    public float getMoney(String username) {
       Wallet userWallet =walletRepo.findByUserEntity_Username(username);
           Wallet wallet =userWallet;
        float walletMoney  =  wallet.getEarnedMoney();
        return walletMoney;
    }

    @Override
    public float walletDiscount(float walletMoney, float totalDiscount) {
        float totalDiscountPrice = 0f;
        if(walletMoney>=20){

             totalDiscountPrice=totalDiscount-walletMoney;
        }
        return totalDiscountPrice;
    }


    public void sendMail(String toEmail, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("anairsreejith1998@gmail.com");
        message.setTo(toEmail);
        message.setSubject(subject); // Set the subject of the email
        message.setText(body);       // Set the email body content
        mailSender.send(message);

    }
   public boolean validateEmail(String email){

       System.out.println(email);
       String emailRegex =  "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$";

       System.out.println(emailRegex);

       if (!Pattern.matches(emailRegex, email)) {

          return false;
       }
       return true;
   }


}
