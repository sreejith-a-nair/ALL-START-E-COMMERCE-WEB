package com.mydemo.demoproject.Repository.admin;

import com.mydemo.demoproject.Entity.Order;
import com.mydemo.demoproject.Entity.Otp;
import com.mydemo.demoproject.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


@Repository
public interface OtpRepo extends JpaRepository<Otp, UUID> {
        Otp findByUserEntity(UserEntity userEntity);



}

