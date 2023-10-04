package com.mydemo.demoproject.service.otp;
//
import com.mydemo.demoproject.Entity.Otp;
import com.mydemo.demoproject.Entity.UserEntity;
import com.mydemo.demoproject.Repository.admin.OtpRepo;
import com.mydemo.demoproject.service.user.UserService;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class OtpServiceImpl implements OtpService {

    @Autowired
    OtpRepo otpRepo;

    @Autowired
    UserService userService;



    @Override
    public void save(Otp otp) {
        otpRepo.save(otp);
    }

    @Override
    public Otp findByUser(UserEntity userEntity) {

        return otpRepo.findByUserEntity(userEntity);
    }


    @Override
    public void delete(Otp oldOtp) {
        otpRepo.delete(oldOtp);

    }

    @Override
    public boolean verifyPhoneOtp(String otp, String phone) {
        UserEntity userEntity =userService.findByPhone(phone);
        System.out.println("in verify o");
        System.out.println(phone);
        System.out.println(userEntity);
        if(userEntity.isVerify()) {
            return true;
        }
        Otp savedOtp=this.findByUser(userEntity);
        if(savedOtp.isOtpRequired()) {
            if(otp.equals(savedOtp.getOtp())) {
                userEntity.setVerify(true);
                userService.save(userEntity);
                this.delete(savedOtp);
                return true;
            }
        }
        return false;
    }

    /*Send otp method*/
    public void sendPhoneOtp(String phoneNumber){

        UserEntity user = userService.findByPhone(phoneNumber);
        Otp savedOtp= this.findByUser(user);

        if(savedOtp != null){
            if (savedOtp.isOtpRequired()) {
                System.out.println("valid otp already exists");
                return;
            }else{
                System.out.println("Deleting expired otp");
                otpRepo.delete(savedOtp);
            }
        }

        String otp = generateOTP();
        Otp generatedOtp = new Otp(otp);
                        System.out.println("otp is: "+otp);
        generatedOtp.setUserEntity(user);
        generatedOtp = otpRepo.save(generatedOtp);
        System.out.println("new otp generated");

//        final String ACCOUNT_SID = "ACddad013c6e6bf651da4c2df9fd5a1d5c";
//        final String AUTH_TOKEN = "5c3523e170b3dd3d21570ee48b116cf0";
//
//        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
//        PhoneNumber from = new PhoneNumber("+12518424102");
//        PhoneNumber to = new PhoneNumber("+919074792270");
//
//
//        String content = "This is your account verification OTP. Valid for 5 minutes. OTP: " + generatedOtp.getOtp();
//        MessageCreator messageCreator = new MessageCreator(to, from,  content);
//        Message res = messageCreator.create();
//        System.out.println(res.getSid());
    }

    @Override
    public UserEntity findByPhone(String  phone) {
        return userService.findByPhone(phone);
    }

    private String generateOTP() {
        // Generate a random 6-digit OTP
        Random random = new Random();
        int otpNumber = 100_000 + random.nextInt(900_000);
        return String.valueOf(otpNumber);
    }
}