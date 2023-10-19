package com.mydemo.demoproject.service.user;

import com.mydemo.demoproject.Entity.ProductInfo;
import com.mydemo.demoproject.Entity.ReturnOrder;
import org.springframework.data.domain.Page;

public interface ReturnOrderService {
    public Page<ReturnOrder> findPaginated(int pageNo, int pageSize);
}
