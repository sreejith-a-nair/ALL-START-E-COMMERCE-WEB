package com.mydemo.demoproject.Repository.shop;

import com.mydemo.demoproject.Entity.WishList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WishListRepo extends JpaRepository<WishList, UUID> {


}
