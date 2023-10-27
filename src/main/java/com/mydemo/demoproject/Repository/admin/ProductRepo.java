package com.mydemo.demoproject.Repository.admin;

import com.mydemo.demoproject.Entity.CategoryInfo;
import com.mydemo.demoproject.Entity.Offer;
import com.mydemo.demoproject.Entity.ProductInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepo extends JpaRepository<ProductInfo,UUID> {




    @Query("SELECT p FROM ProductInfo p JOIN FETCH p.category")
    List<ProductInfo> findAllWithCategories();



    /*Search query*/
    @Query(value = "SELECT * FROM product_info WHERE product_name LIKE %:keyword%", nativeQuery = true)
    List<ProductInfo> findByProductNameKeyword (@Param("keyword") String keyword);

     /*get productByCategory*/
    Optional<ProductInfo>findByCategory(UUID uuid);

    Optional<ProductInfo>findByStock(UUID uuid);


    List<ProductInfo> findByCategory_Uuid(UUID categoryUuid);


    /*product search*/
    Page<ProductInfo> findBynameContaining(String name, Pageable pageable);


    Page<ProductInfo> findByCategory_Uuid(UUID categoryUuid, Pageable pageable);



    Page<ProductInfo> findByBrand_Uuid(UUID brandUuid, Pageable pageable);

    Page<ProductInfo> findByPriceBetween(double minPrice, double maxPrice, Pageable pageable);


@Query("SELECT p FROM ProductInfo p WHERE p.price BETWEEN :minPrice AND :maxPrice")
Page<ProductInfo> findProductsInPriceRange(
        @Param("minPrice") float minPrice,
        @Param("maxPrice") float maxPrice,
        Pageable pageable
);

    @Query("SELECT p FROM ProductInfo p JOIN p.offer o WHERE o.CategoryOffPercentage BETWEEN :minPercentage AND :maxPercentage")
    Page<ProductInfo> findProductsInOfferPercentageRange(
            @Param("minPercentage") int minPercentage,
            @Param("maxPercentage") int maxPercentage,
            Pageable pageable
    );


    boolean existsByUuid(UUID uuid);

    Page<ProductInfo> findByNameContainingIgnoreCase(String name, Pageable pageable);

}
