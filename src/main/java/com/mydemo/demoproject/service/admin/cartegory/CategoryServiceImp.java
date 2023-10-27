package com.mydemo.demoproject.service.admin.cartegory;

import com.mydemo.demoproject.Entity.CategoryInfo;
import com.mydemo.demoproject.Entity.ProductInfo;
import com.mydemo.demoproject.Repository.admin.CategoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CategoryServiceImp implements CategoryService {


    @Autowired
    CategoryRepo categoryRepo;



    @Override
    public Optional<CategoryInfo> getCategory(UUID uuid) {
        return categoryRepo.findById(uuid);
    }

    @Override
    public List<CategoryInfo> findAll() {

        return categoryRepo.findAll();
    }


    /*AddCategory Service */

    @Override
    public String addCategory(CategoryInfo categoryInfo, Model model) {

        Optional<CategoryInfo> existingUser = categoryRepo.findBycategoryname(categoryInfo.getCategoryname());
        if (existingUser.isPresent()) {
            model.addAttribute("errorMessage", "category already exists");
            return "admin/add-category";
        } else {
            categoryRepo.save(categoryInfo);
            return "admin/dashboard";

        }

    }

    @Override
    public List<CategoryInfo> loadAllCategory() {
        List  <CategoryInfo> category;
        category = categoryRepo.findAll();
        return category;
    }

    /*pagination search*/
    @Override
    public Page<CategoryInfo> searchCategory(String name, Pageable pageable) {
        return categoryRepo.findBycategorynameContaining( name,  pageable);
    }




    /*Pagination */
    @Override
    public Page<CategoryInfo> findPaginated(int pageNo, int pageSize) {
        Pageable pageable= PageRequest.of(pageNo-1,pageSize);
        return this.categoryRepo.findAll(pageable);
    }

    /*search pagination*/


    /*pagination end*/

    @Override
    public Optional<CategoryInfo> getCategoryById(UUID uuid) {
        return categoryRepo.findById(uuid);
    }

    /*ENABLE AND DISABLE*/
    @Override
    public void enableCategory(UUID uuid) {
        Optional<CategoryInfo> categoryInfo =categoryRepo.findById(uuid);
    if(categoryInfo.isPresent()){
      CategoryInfo category = categoryInfo.get();
      category.setEnable(true);
      categoryRepo.save(category);
  }

    }

    @Override
    public void disableCategory(UUID uuid) {
    Optional<CategoryInfo> categoryInfo  = categoryRepo.findById(uuid);
    if(categoryInfo.isPresent()){
     CategoryInfo category= categoryInfo.get();
     category.setEnable(false);
     categoryRepo.save(category);
    }

    }

    @Override
    public Page<CategoryInfo> getCategoryes() {
        return (Page<CategoryInfo>) categoryRepo.findAll();

    }

    /*UPDATE*/


    /*category save */

    @Override
    public void save(CategoryInfo categoryInfo) {


        categoryRepo.save(categoryInfo);


    }

    /*Find all categoryName*/
    @Override
    public List<CategoryInfo> findAllCategory() {
       List  <CategoryInfo> categoryList;
        categoryList =  categoryRepo.findAll();
        return categoryList;
    }

    @Override
    public int findCategoryCount() {
        int count =0;
        List<CategoryInfo>categories=categoryRepo.findAll();
        for(CategoryInfo categoryInfo:categories){
            System.out.println(categoryInfo.getCategoryname());
            count++;
        }
        return count;
    }



}