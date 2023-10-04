package com.mydemo.demoproject.Repository.admin;

import com.mydemo.demoproject.Entity.CategoryInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepo extends JpaRepository<CategoryInfo,UUID> {


 Optional<CategoryInfo> findBycategoryname(String category);






//    @Query(value = "SELECT * FROM category_info WHERE category_name LIKE %:keyword%", nativeQuery = true)
//    List<CategoryInfo> findByCategoryKeyword (@Param("keyword") String keyword);


    Page<CategoryInfo> findBycategorynameContaining(String keyword, Pageable pageable);




//    void deleteById(Long id);

//    Optional<ProductInfo> findById(UUID productId);

    Optional<CategoryInfo> findById(UUID uuid);



}
