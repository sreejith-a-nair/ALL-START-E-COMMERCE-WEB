package com.mydemo.demoproject.service.shop;

import com.mydemo.demoproject.Entity.Address;
import com.mydemo.demoproject.Entity.ProductInfo;
import com.mydemo.demoproject.Repository.shop.AddressRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AddressServiceImp implements AddressService{

    @Autowired
    AddressRepo addressRepo;
    @Override
    public void saveToAddress(Address address) {
        addressRepo.save(address);
    }

    @Override
    public List<Address> findByUserEntity_Usernames(String username) {
        List<Address> addressList= addressRepo.findByUserEntity_Username(username);
//        System.out.println("find user ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;"+cartList);
        return addressList;
    }

    @Override
    public Optional<Address> findAddressById(UUID uuid) {
        return addressRepo.findById(uuid);
    }

    @Override
    public void removeAddress(UUID uuid) {
        addressRepo.deleteById(uuid);
    }


    @Override
    public void blockAddress(UUID uuid) {
        Optional<Address> addressInfo =addressRepo.findById(uuid);
        if (addressInfo.isPresent()) {
            System.out.println("blocked product>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            Address address = addressInfo.get();
            address.setEnabled(false);
            addressRepo.save(address);
        } else {
            throw new EntityNotFoundException("User not found with ID: " + uuid);
        }
    }

    @Override
    public void unBlockAddress(UUID uuid) {
        Optional<Address> addressInfo =addressRepo.findById(uuid);
        if (addressInfo.isPresent()) {
            System.out.println("blocked product>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            Address address = addressInfo.get();
            address.setEnabled(true);
            addressRepo.save(address);
        } else {
            throw new EntityNotFoundException("User not found with ID: " + uuid);
        }
    }
}
