package com.mydemo.demoproject.Repository.admin;

import com.mydemo.demoproject.Entity.Brand;
import com.mydemo.demoproject.Entity.CategoryInfo;
import com.mydemo.demoproject.Entity.ProductInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BrandRepo extends JpaRepository<Brand, UUID> {

    @Query(value = "SELECT * FROM brand WHERE name LIKE %:keyword%", nativeQuery = true)
    List<Brand> findByBrandKeyword (@Param("keyword") String keyword);


   Optional<Brand>findById(UUID uuid);

}
