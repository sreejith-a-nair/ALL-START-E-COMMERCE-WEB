package com.mydemo.demoproject.Repository.shop;

import com.mydemo.demoproject.Entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WalletRepo extends JpaRepository<Wallet, UUID> {

    Wallet findByUserEntity_Username(String username);
}
