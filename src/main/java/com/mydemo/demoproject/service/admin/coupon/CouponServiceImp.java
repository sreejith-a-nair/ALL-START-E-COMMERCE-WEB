package com.mydemo.demoproject.service.admin.coupon;

import com.mydemo.demoproject.Entity.CategoryInfo;
import com.mydemo.demoproject.Entity.Coupon;
import com.mydemo.demoproject.Entity.ProductInfo;
import com.mydemo.demoproject.Repository.admin.CouponRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CouponServiceImp implements  CouponService{

    @Autowired
    CouponRepo couponRepo;


    @Override
    public void saveCoupon(Coupon coupon) {
        coupon.setEnabled(true);
        couponRepo.save(coupon);
    }

    @Override
    public List<Coupon> findAll() {
        return couponRepo.findAll();
    }

    @Override
    public void blockCoupon(UUID uuid) {
        Optional<Coupon> coupon =couponRepo.findById(uuid);
        if (coupon.isPresent()) {
            Coupon couponData = coupon.get();
            couponData.setEnabled(false);
            couponRepo.save(couponData);
        } else {
            throw new EntityNotFoundException("User not found with ID: " + uuid);
        }

    }

    @Override
    public void unblockCoupon(UUID uuid) {
        Optional<Coupon> coupon =couponRepo.findById(uuid);
        if (coupon.isPresent()) {
            Coupon couponData = coupon.get();
            couponData.setEnabled(true);
            couponRepo.save(couponData);
        } else {
            throw new EntityNotFoundException("User not found with ID: " + uuid);
        }
    }

    @Override
    public Optional<Coupon> getCouponBYId(UUID uuid) {
        return couponRepo.findById(uuid);
    }

    @Override
    public Page<Coupon> findPaginated(int pageNo, int pageSize) {
        Pageable pageable= PageRequest.of(pageNo-1,pageSize);
        return this.couponRepo.findAll(pageable);
    }
}
