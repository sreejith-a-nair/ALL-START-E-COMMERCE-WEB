package com.mydemo.demoproject.Repository.admin;

import com.mydemo.demoproject.Entity.CategoryInfo;
import com.mydemo.demoproject.Entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepo extends JpaRepository<UserEntity,UUID> {


    Optional<UserEntity> findByUsernameNot(String username);


    Optional<UserEntity> findByusername(String username);

    Optional<UserEntity>findByemail(String email);

    UserEntity findByPhone(String phone);

    public  UserEntity findByContact(Long phone);


    UserEntity findByNewUserReferral(String referralCode);

    Page<UserEntity> findByUsernameContainingIgnoreCase(String keyword, Pageable pageable);

}
