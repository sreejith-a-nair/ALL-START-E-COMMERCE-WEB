package com.mydemo.demoproject.service.shop;

import com.mydemo.demoproject.Entity.Address;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AddressService {
    void saveToAddress(Address address);
    List<Address> findByUserEntity_Usernames (String username);

   Optional<Address> findAddressById(UUID uuid);

   void  removeAddress(UUID uuid);

     void blockAddress(UUID uuid);
     void unBlockAddress(UUID uuid);

}
