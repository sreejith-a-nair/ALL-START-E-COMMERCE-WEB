package com.mydemo.demoproject.service.admin.product;

import com.mydemo.demoproject.Entity.Brand;
import com.mydemo.demoproject.Entity.CategoryInfo;
import com.mydemo.demoproject.Entity.Offer;
import com.mydemo.demoproject.Entity.ProductInfo;
import com.mydemo.demoproject.Repository.admin.BrandRepo;
import com.mydemo.demoproject.Repository.admin.CategoryRepo;
import com.mydemo.demoproject.Repository.admin.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductServiceImp implements ProductService {
    @Autowired
    ProductRepo productRepo;

     @Autowired
     ImageService imageService;


    @Autowired
    CategoryRepo categoryRepo;

    @Autowired
    BrandRepo brandRepo;

    /* delete image*/
    @Override
    public void deleteImages(UUID uuid) {
       imageService .deleteImages(uuid);
    }

    /*Find all product*/
    @Override
    public List<ProductInfo> findAll() {
        return productRepo.findAll();
    }

    /*Save product*/
    @Override
    public ProductInfo update(ProductInfo products) {
        return productRepo.save(products);
    }

    @Override
    public List<ProductInfo> loadAllProduct() {
        List  <ProductInfo> productList;
        productList = productRepo.findAll();
        return productList;
    }

    /*new load*/
    @Override
    public Page<ProductInfo> loadAllProducts(Pageable pageable) {
        return productRepo.findAll(pageable);

    }

    /*pagination*/
    @Override
    public Page<ProductInfo> findPaginated(int pageNo, int pageSize) {
        Pageable pageable= PageRequest.of(pageNo-1,pageSize);
        return this.productRepo.findAll(pageable);
    }
    /*pagination end*/

    /*Search product*/
    @Override
    public List<ProductInfo> searchProductName(String keyword) {
        return productRepo.findByProductNameKeyword(keyword);
    }


    @Override
    public Optional<ProductInfo> getProduct(UUID uuid) {
        return productRepo.findById(uuid);
    }


    @Override
    public List<ProductInfo> getCategoryByProduct(UUID uuid) {
        return productRepo.findByCategory_Uuid(uuid);
    }


    @Override
    public List<ProductInfo> findByNameLike(String key) {
        return productRepo.findByProductNameKeyword(key);
    }


    /* /*BLOCK PRODUCT*/
    @Override
    public void blockProduct(UUID uuid) {
        Optional<ProductInfo> productInfo =productRepo.findById(uuid);
        if (productInfo.isPresent()) {
            ProductInfo product = productInfo.get();
            product.setEnable(false);
            productRepo.save(product);
        } else {
            throw new EntityNotFoundException("User not found with ID: " + uuid);
        }
    }
    /*UN-BLOCK PRODUCT*/
    @Override
    public void unblockProduct(UUID uuid) {
        Optional<ProductInfo> productInfo =productRepo.findById(uuid);
        if (productInfo.isPresent()) {
            ProductInfo product = productInfo.get();
            product.setEnable(true);
            productRepo.save(product);
        } else {
            throw new EntityNotFoundException("User not found with ID: " + uuid);
        }
    }

/*find all page*/
    @Override
    public Page<ProductInfo> findAllByPage(int pageNo, int pageSize, String field, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(field).ascending() :
                Sort.by(field).descending();
        Pageable pageable = PageRequest.of(pageNo-1, pageSize,sort);
        return productRepo.findAll(pageable);
    }


    public ProductInfo addProduct(ProductInfo product, UUID categoryId,UUID brandId) throws ChangeSetPersister.NotFoundException {

        CategoryInfo category = categoryRepo.findById(categoryId)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        Brand brands=brandRepo.findById(brandId)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        product.setCategory(category);
        product.setBrand(brands);
        return productRepo.save(product);
}

    @Override
    public void updateStock(UUID uuid, Long stock) {
        Optional<ProductInfo> optionalProduct = productRepo.findById(uuid);
        System.out.println(optionalProduct);
        if (optionalProduct.isPresent()) {
            ProductInfo product = optionalProduct.get();


            product.setStock(stock);

            productRepo.save(product);
        }
    }

    public Page<ProductInfo> searchProduct(String searchKey, Pageable pageable) {
        return productRepo.findBynameContaining(searchKey, pageable);
    }

    public Page<ProductInfo> getCategoryProducts(UUID selectedCategoryUuid, Pageable pageable) {
        return productRepo.findByCategory_Uuid(selectedCategoryUuid, pageable);
    }

    @Override
    public Page<ProductInfo> getProductsInPriceRange(float minPrice, float maxPrice, Pageable pageable) {

         return  productRepo.findProductsInPriceRange(minPrice, maxPrice,pageable);


    }

    @Override
    public Page<ProductInfo> getProductsInOfferPercentageRange(int minPercentage, int maxPercentage, Pageable pageable) {
        return productRepo.findProductsInOfferPercentageRange(minPercentage, maxPercentage, pageable);
    }

    @Override
    public float getDiscountPrice(float productPrice, int offerPercent) {
        float discountPrice=productPrice-(productPrice*offerPercent/100);
        return discountPrice;
    }


    @Override
    public int findProductCount() {
        List <ProductInfo> productInfo=productRepo.findAll();
        int count =0;
        for(ProductInfo products : productInfo){
            System.out.println(products.getName());
            count++;
        }
        return count;
    }


    public Page<ProductInfo> searchProductPageable(String keyword, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return productRepo.findByNameContainingIgnoreCase(keyword, pageable);
    }
}
