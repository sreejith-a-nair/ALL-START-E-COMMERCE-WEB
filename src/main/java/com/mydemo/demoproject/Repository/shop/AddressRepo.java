package com.mydemo.demoproject.Repository.shop;

import com.mydemo.demoproject.Entity.Address;
import com.mydemo.demoproject.Entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AddressRepo extends JpaRepository<Address, UUID> {
    List<Address> findByUserEntity_Username(String username);
}
