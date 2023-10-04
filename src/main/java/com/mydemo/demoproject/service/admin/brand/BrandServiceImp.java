package com.mydemo.demoproject.service.admin.brand;

import com.mydemo.demoproject.Entity.Brand;
import com.mydemo.demoproject.Entity.ProductInfo;
import com.mydemo.demoproject.Repository.admin.BrandRepo;
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
public class BrandServiceImp implements BrandService{

    @Autowired
    BrandRepo brandRepo;
    @Override
    public List<Brand> findAll() {
        return brandRepo.findAll();
    }

    @Override
    public void save(Brand brand) {

        brandRepo.save(brand);
    }

    @Override
    public List<Brand> searchBrand(String keyword) {
        return  brandRepo.findByBrandKeyword(keyword);
    }

    @Override
    public List<Brand> loadAllBrand() {
        return brandRepo.findAll();
    }

    @Override
    public Page<Brand> findPaginated(int pageNo, int pageSize) {
        Pageable pageable= PageRequest.of(pageNo-1,pageSize);
        return this.brandRepo.findAll(pageable);
    }

    @Override
    public Optional<Brand> getBrandById(UUID uuid) {
        return brandRepo.findById(uuid);
    }

    /*block /unblock brand*/
    @Override
    public void blockBrand(UUID uuid) {

        Optional<Brand> brand =brandRepo.findById(uuid);
        if (brand.isPresent()) {
            System.out.println("Blocked brand>>>>>>>>>>>>>>>>>>");
            Brand brandInfo = brand.get();
            brandInfo.setEnable(false);
            brandRepo.save(brandInfo);
        } else {
            throw new EntityNotFoundException("User not found with ID: " + uuid);
        }
    }
    @Override
    public void unblockBrand(UUID uuid) {
        Optional<Brand> brand =brandRepo.findById(uuid);
        if (brand.isPresent()) {
            System.out.println("Unblocked brand>>>>>>>>>>>");
            Brand brandInfo = brand.get();
            brandInfo.setEnable(true);
            brandRepo.save(brandInfo);
        } else {
            throw new EntityNotFoundException("User not found with ID: " + uuid);
        }
    }

}
