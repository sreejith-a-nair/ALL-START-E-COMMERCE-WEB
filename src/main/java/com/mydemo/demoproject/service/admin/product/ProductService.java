package com.mydemo.demoproject.service.admin.product;

import com.mydemo.demoproject.Entity.Offer;
import com.mydemo.demoproject.Entity.ProductInfo;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductService {

    /*delete image*/

   public void deleteImages(UUID uuid);


    List<ProductInfo> findAll();
    /*save */
    ProductInfo update (ProductInfo products);

    /*Load all product*/

    List<ProductInfo> loadAllProduct() ;



    /*load all product new*/

    Page<ProductInfo> loadAllProducts(Pageable pageable) ;

    /*new*/

    /*pagination*/
     Page<ProductInfo>findPaginated(int pageNo, int pageSize);
     /*end pagination*/


    /* Search product method*/
    List<ProductInfo>  searchProductName(String keyword);


    /*get product*/
    Optional<ProductInfo> getProduct(UUID uuid);

/*se*/
 List<ProductInfo> getCategoryByProduct(UUID uuid);

    List<ProductInfo> findByNameLike(String key);

     void blockProduct(UUID uuid);

    void unblockProduct(UUID uuid);

    /*find by all page*/
    public Page<ProductInfo> findAllByPage(int pageNo, int pageSize, String field, String sortDirection) ;

    public ProductInfo addProduct(ProductInfo product, UUID categoryId,UUID brandId) throws ChangeSetPersister.NotFoundException;

    void updateStock(UUID uuid, Long stock);


    Page<ProductInfo> searchProduct(String searchKey, Pageable pageable);

     Page<ProductInfo> getCategoryProducts(UUID selectedCategoryUuid, Pageable pageable);

  Page<ProductInfo> getProductsInPriceRange(float minPrice, float maxPrice, Pageable pageable);



    Page<ProductInfo> getProductsInOfferPercentageRange(int minPrice, int maxPrice, Pageable pageable);

       float  getDiscountPrice(float productPrice,int offerPercent);
}
