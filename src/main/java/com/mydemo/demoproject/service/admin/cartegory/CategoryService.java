package com.mydemo.demoproject.service.admin.cartegory;

import com.mydemo.demoproject.Entity.CategoryInfo;
import com.mydemo.demoproject.Entity.ProductInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryService {

    Optional<CategoryInfo> getCategory(UUID uuid);

    List<CategoryInfo> findAll();


/*add-category repo*/
    String addCategory(CategoryInfo categoryInfo, Model model);


/* Search method*/
//List<CategoryInfo> searchCategory(String keyword);


    /*Load category*/
     List<CategoryInfo> loadAllCategory() ;

     /*pagination ion search*/
    Page<CategoryInfo>searchCategory(String name, Pageable pageable);


     /*Pagination */

    public Page<CategoryInfo> findPaginated(int pageNo, int pageSize);


    /*find by id*/
    public  Optional<CategoryInfo> getCategoryById(UUID uuid);



    /*enable /disable*/
    void enableCategory(UUID uuid);


    void disableCategory(UUID uuid);

    public Page<CategoryInfo> getCategoryes();

    /*Save*/
   void save (CategoryInfo categoryInfo);


   /* FindByCategory  */
    List<CategoryInfo> findAllCategory();


}
