package com.mydemo.demoproject.Repository.shop;

import com.mydemo.demoproject.Entity.CancelOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface CancelOrderRepo extends JpaRepository<CancelOrder, UUID> {



}
