package com.mydemo.demoproject.Repository.admin;

import com.mydemo.demoproject.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AdminRepo extends JpaRepository<UserEntity,UUID> {


    List<UserEntity> findByUsernameNot(String username);


    Optional<UserEntity> findByusername(String username);

    public UserEntity findByPhone(String phone);



    @Query(value = "select * from User_Entity where user_name like %:keyword%", nativeQuery = true)
    List<UserEntity> findByKeyword(@Param("keyword") String keyword);

    Optional<UserEntity> findById(UUID uuid);

//    or first_name like  %:keyword%"

}
