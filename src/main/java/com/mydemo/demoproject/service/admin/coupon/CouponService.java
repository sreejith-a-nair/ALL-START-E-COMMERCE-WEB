package com.mydemo.demoproject.service.admin.coupon;

import com.mydemo.demoproject.Entity.CategoryInfo;
import com.mydemo.demoproject.Entity.Coupon;
import com.mydemo.demoproject.Entity.ProductInfo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CouponService {

    void  saveCoupon(Coupon coupon);


    List<Coupon> findAll();

/*blockCoupon*/
     void blockCoupon(UUID uuid);

      void unblockCoupon(UUID uuid);

      /*Find coupon byId*/
     Optional<Coupon> getCouponBYId(UUID uuid);

     /*pagination*/
     Page<Coupon> findPaginated(int pageNo, int pageSize);
    /*pagination end*/
}
