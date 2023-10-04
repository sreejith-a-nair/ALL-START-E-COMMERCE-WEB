package com.mydemo.demoproject.Repository.admin;

import com.mydemo.demoproject.Entity.Order;
import com.mydemo.demoproject.Entity.OrderItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface OrderItemRepo extends JpaRepository< OrderItems,UUID> {

}
