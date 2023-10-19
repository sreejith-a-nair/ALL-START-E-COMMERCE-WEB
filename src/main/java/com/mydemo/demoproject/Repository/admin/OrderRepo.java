package com.mydemo.demoproject.Repository.admin;

import com.mydemo.demoproject.Entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Repository
public interface OrderRepo extends JpaRepository<Order,UUID> {

    List<Order> findByUserEntity_Username(String username);




    /*Search query*/
//    @Query("SELECT order FROM Order order JOIN order.userEntity userEntity WHERE userEntity.username LIKE %:keyword%")
//    List<Order> findByUserNameKeyword(@Param("keyword") String keyword);
    @Query("SELECT orders FROM Order orders WHERE orders.userEntity.username LIKE %:keyword%")
    List<Order> findByUserNameKeyword(@Param("keyword") String keyword);

    List<Order> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);
}
