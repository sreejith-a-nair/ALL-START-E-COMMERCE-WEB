package com.mydemo.demoproject.service.admin.brand;

import com.mydemo.demoproject.Entity.Brand;
import com.mydemo.demoproject.Entity.ProductInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BrandService {
    List<Brand> findAll();

    void save (Brand brand);

    List<Brand> searchBrand(String keyword);

    List<Brand> loadAllBrand() ;

    /*pagination*/
    Page<Brand> findPaginated(int pageNo, int pageSize);
    /*pagination end*/

    /*edit brand*/
    Optional<Brand> getBrandById(UUID uuid);

    /*block brand /unblock brand*/

    void blockBrand(UUID uuid);

    void unblockBrand(UUID uuid);


    /*new*/

    Page<ProductInfo> getBrandProducts(UUID selectedBrandUuid, Pageable pageable);

    int findBrandCount();

}
