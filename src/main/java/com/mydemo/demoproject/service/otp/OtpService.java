package com.mydemo.demoproject.service.otp;

import com.mydemo.demoproject.Entity.Otp;
import com.mydemo.demoproject.Entity.UserEntity;
import org.springframework.stereotype.Service;

@Service
public interface OtpService {

    void save(Otp otp);

    Otp findByUser(UserEntity userEntity);

    void delete(Otp oldOtp);

    boolean verifyPhoneOtp(String otp, String phone);

    void sendPhoneOtp(String phoneNumber);

    UserEntity findByPhone(String phone);

}
