package com.mydemo.demoproject.Repository.shop;

import com.mydemo.demoproject.Entity.ReturnOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface ReturnOrderRepo extends JpaRepository<ReturnOrder, UUID> {


}
