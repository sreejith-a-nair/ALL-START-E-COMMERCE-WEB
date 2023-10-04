package com.mydemo.demoproject.Entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

    @Entity
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString

    public class Otp {

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO, generator = "uuid2")
        @GenericGenerator(name = "uuid2", strategy = "uuid2")
        @Type(type = "org.hibernate.type.UUIDCharType")
        private UUID uuid;

        private Date otpRequestTime;

        private String otp;

        @OneToOne
        @JoinColumn(name = "user_id")
        private UserEntity userEntity;

        private static final long OTP_VALID_DURATION =5*60*1000;
        public boolean isOtpRequired() {
            if(this.getOtp()==null) {
                return false;
            }
            long currentTimeInMillis=System.currentTimeMillis();
            long otpRequestedTimelineMillis=this.otpRequestTime.getTime();
            return otpRequestedTimelineMillis + OTP_VALID_DURATION >= currentTimeInMillis;

        }
        public Otp(String otp) {
            this.otp=otp;
            this.otpRequestTime=new Date();
        }

    }
