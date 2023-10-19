package com.mydemo.demoproject.Repository.admin;

import com.mydemo.demoproject.Entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface CouponRepo extends JpaRepository<Coupon, UUID> {

  List<Coupon>findByCode(String code);


}
