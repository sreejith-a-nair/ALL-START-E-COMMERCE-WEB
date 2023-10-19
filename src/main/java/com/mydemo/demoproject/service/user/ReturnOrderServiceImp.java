package com.mydemo.demoproject.service.user;

import com.mydemo.demoproject.Entity.ProductInfo;
import com.mydemo.demoproject.Entity.ReturnOrder;
import com.mydemo.demoproject.Repository.shop.ReturnOrderRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ReturnOrderServiceImp implements  ReturnOrderService {

    @Autowired
    ReturnOrderRepo returnOrderRepo;

    @Override
    public Page<ReturnOrder> findPaginated(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        return this.returnOrderRepo.findAll(pageable);
    }
}