package com.mydemo.demoproject.controller.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mydemo.demoproject.Entity.*;
import com.mydemo.demoproject.Repository.admin.ProductRepo;
import com.mydemo.demoproject.service.admin.brand.BrandService;
import com.mydemo.demoproject.service.admin.cartegory.CategoryService;
import com.mydemo.demoproject.service.admin.product.ImageService;
import com.mydemo.demoproject.service.admin.product.ProductServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/product")
public class ProductController {




    @Autowired
    CategoryService categoryService;

    @Autowired
    ImageService imageService;

     @Autowired
     ProductServiceImp productService;

     @Autowired
    BrandService brandService;

    @GetMapping("/home")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String showAllProduct(Model model){
        return   findPaginated(1,model);
    }


    /*pagination*/

    @GetMapping("/page/{pageNo}")
    public String findPaginated(@PathVariable(value = "pageNo") int pageNo,Model model){
        int pageSize=5;
        Page<ProductInfo> page=productService.findPaginated(pageNo,pageSize);
        List<ProductInfo>productInfo=page.getContent();

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("productInfo", productInfo);

        return "admin/product";
    }

/*pagination end*/

    /*3 save / update*/
    @PostMapping("/addCategory")
    public String addCategory(@ModelAttribute CategoryInfo categoryInfo,Model model) {

        try {
            categoryService.save(categoryInfo);
            return "redirect:/product/addProduct";
        } catch (Exception ex) {
            return "redirect:/product/addProduct?error=categoryExists";
        }
    }

    /*Get add page*/
    @GetMapping("/addProduct")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String addProduct( @RequestParam(value="error",required=false)String error,Model model )throws JsonProcessingException {
        List<CategoryInfo> categories = categoryService.findAllCategory();
        List<Brand>brands=brandService.loadAllBrand();

        if (error != null && error.equals("productExists")) {
            model.addAttribute("error", "Product already exists.");
        }
        if (error != null && error.equals("categoryExists")) {
            model.addAttribute("error", "Category already exists.");
}
        model.addAttribute("categoryInfo",categories);
        model.addAttribute("brands",brands);

        return "admin/add-product";
    }

/*Delete image*/
    @GetMapping("deleteImage/{uuid}")
    public String deleteImage(@PathVariable UUID uuid)
    {
     productService.deleteImages(uuid);
     return "redirect:/product/home";
    }


    @GetMapping("addStock/{uuid}")
    public String addStockForm(@PathVariable UUID uuid, Model model) {

        Optional<ProductInfo> productInfo = productService.getProduct(uuid);
        System.out.println(productInfo);
        if (productInfo.isPresent()) {
            System.out.println(productInfo);
            model.addAttribute("productInfo", productInfo.get());
        } else {

            System.out.println("error");
        }
        return "admin/add-stock";
    }


    @PostMapping("/save")
    public String saveProduct(@ModelAttribute("productInfo") ProductInfo productInfo,
                              Model model) {
        System.out.println("product;;;;"+productInfo);
        Optional<ProductInfo> existingProduct = productService.getProduct(productInfo.getUuid());
        System.out.println("product;;;;"+existingProduct);

        if (existingProduct.isPresent()) {
            // Calculate the new stock by adding the existing stock with the input new stock
            System.out.println("product in if;;;;"+existingProduct);
            Long currentStock = existingProduct.get().getStock();
            Long newStock = productInfo.getStock();
            Long totalStock = currentStock + newStock;
            System.out.println("Sum of stock..."+totalStock);

            // Update the product's stock quantity
            productInfo.setStock(totalStock);
            productService.updateStock(productInfo.getUuid(), totalStock);

            // Redirect to a success page or wherever you need
            return "redirect:/product/home";
        } else {
            // Handle the case where the product is not found
            return "redirect:/product/not-found";
        }
    }



/*  Post  To add new products*/
    @PostMapping("/addProducts")
    public String addProduct(@RequestParam("uuid")  UUID uuid,
                             @RequestParam("brandUuid")UUID brandUuid,
                             @RequestParam("images") List<MultipartFile> imageFiles,
                             @RequestParam("name") String name,
                             @RequestParam("price") Float price,
                             @RequestParam("stock") Long stock,
                             @RequestParam("description") String description) throws IOException {
        try {
//            System.out.println("products.................................p");
            ProductInfo product = new ProductInfo();
            product.setName(name);
            product.setPrice(price);
            product.setStock(stock);
            product = productService.addProduct(product,uuid,brandUuid);
            List<Image> images = new ArrayList<>();
            if(!imageFiles.get(0).getOriginalFilename().equals("")){
                for (MultipartFile image : imageFiles) {
                    String fileLocation = handleFileUpload(image); // Save the image and get its file location
                    Image imageEntity = new Image(fileLocation,product); // Create an Image entity with the file location
                    imageEntity = imageService.save(imageEntity);
                    images.add(imageEntity); // Add the Image entity to the Product's list of images
                }
            }
            product.setDescription(description);
            if(!imageFiles.get(0).getOriginalFilename().equals("")){
                product.setImages(images);
            }
            product = productService.addProduct(product, uuid,brandUuid);
            return "redirect:/product/home";
/*
            "redirect:/admin/product"
*/
        }
        catch (Exception ex) {
            return "redirect:/product/addProduct?error=productExists";
     }
}


    /* @Get Update product*/
    @GetMapping("/update/{uuid}")
    public String update(@PathVariable UUID uuid, Model model) {
        Optional<ProductInfo> products = productService.getProduct(uuid);

        if (products.isPresent()) {
//            System.out.println("is present");
            ProductInfo productInfo = products.get();
            model.addAttribute("productInfo",productInfo);
//            model.addAttribute("brands",brands);
            return "admin/edit-product";
        } else {
            return String.valueOf(model.addAttribute("error message", "Invalid valid data"));
        }
    }

/* @Post Update products*/

@PostMapping("/update")
public String updateProduct(@RequestParam("uuid") UUID productUuid,
                            @RequestParam("brandUuid")UUID brandUuid,
                            @RequestParam("name") String name,
                            @RequestParam("categoryUuid") UUID categoryUuid,
                            @RequestParam("description") String description,
                            @RequestParam("price") Float price,
                            @RequestParam("stock") Long stock,
                            @RequestParam(value = "newImages" , required = false) List<MultipartFile> newImages) throws IOException {


     Optional<Brand> brand =brandService.getBrandById(brandUuid);
     Brand brandInfo= brand.get();
     System.out.println("select brand>>>"+brandInfo);

    System.out.println("image Update: "+newImages);
    ProductInfo updatedProduct = new ProductInfo();
    updatedProduct.setUuid(productUuid);
    updatedProduct.setName(name);
    updatedProduct.setStock(stock);
    updatedProduct.setPrice(price);
    updatedProduct.setCategory(categoryService.getCategoryById(categoryUuid).orElseThrow());
    updatedProduct.setDescription(description);
    updatedProduct.setEnable(true );
    updatedProduct.setBrand(brandInfo);
    productService.update(updatedProduct);
    //save new images

    if(newImages!=null) {
        for (MultipartFile image : newImages) {
            if (image.getOriginalFilename() != "") {
                String fileLocation = handleFileUpload(image);
                Image imageEntity = new Image(fileLocation, productService.getProduct(productUuid).orElseThrow());
                imageService.save(imageEntity);
            }
        }
    }
        return "redirect:/product/home";
}




    private String handleFileUpload(MultipartFile file) throws IOException {

        String rootPath = System.getProperty("user.dir");
        String uploadDir = rootPath + "/src/main/resources/static/uploads";

        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }


        String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();


        String filePath = uploadDir + "/" + fileName;
        Path path = Paths.get(filePath);
        System.out.println(path);
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);


        return fileName;
    }

    private void handleDelete(String fileName) throws IOException {

        String rootPath = System.getProperty("user.dir");
        String uploadDir = rootPath + "/src/main/resources/static/uploads";


        String filePath = uploadDir + "/" + fileName;


        File file = new File(filePath);


        if (file.exists()) {

            file.delete();
            System.out.println("File deleted successfully!");
        } else {
            System.out.println("File not found!");
        }
    }

    /*Upload image*/
    @PostMapping(value = "/uploadImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadImage(@RequestPart("image") MultipartFile imageFile,
                                              @RequestPart("pUuid") String pUuid) throws IOException {

        ProductInfo product = productService.getProduct(UUID.fromString(pUuid)).orElseThrow();
        Image image = new Image();
        image.setProduct_id(product);
        image.setFileName(handleFileUpload(imageFile));
        image = imageService.save(image);

        return ResponseEntity.ok("Image uploaded successfully");
}


    /* @Get  Update / edit product*/
    @GetMapping("/edit/{uuid}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String editCategory(@PathVariable UUID uuid, Model model) {
//        System.out.println("edit product method.>>>>>>>>>>>>>>.............." + uuid);
        Optional<ProductInfo> productInfoOptional = productService.getProduct(uuid);

        System.out.println("hai");
        List<Brand>brands=brandService.loadAllBrand();
        System.out.println("product >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+brands);

        if (productInfoOptional.isPresent()) {
//            System.out.println("is present..............................");
            ProductInfo productInfo = productInfoOptional.get();
            model.addAttribute("productInfo",productInfo);
            model.addAttribute("brands",brands);
            return "admin/edit-product";
        } else {
            return String.valueOf(model.addAttribute("error message", "Invalid valid data"));
        }
    }

    /*search*/
    @GetMapping(value = "/search", params = "keyword")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String searchProduct(Model model, @Param("keyword") String keyword) {
//        System.out.println("search>begin.....>>>>>>>");
        try {
            List<ProductInfo> productInfo;

            if (keyword != null && !keyword.isEmpty()) {
                productInfo = productService.searchProductName(keyword);
                //System.out.println("search>>>>if" + categoryList);

            } else {
                //System.out.println("search--else------->>>");

                productInfo = productService.loadAllProduct();
            }

            model.addAttribute("productInfo", productInfo);

            return "admin/product";

        } catch (Exception e) {

            e.printStackTrace();

            model.addAttribute("errorMessage", "An error occurred while processing your request.");
            return "error";
        }

    }


   /*Block Product*/
    @GetMapping("/block/{uuid}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String blockProduct(@PathVariable UUID uuid)
    {
        productService.blockProduct(uuid);
        return "redirect:/product/home";
    }


    /*Un-block Product*/
    @GetMapping("/unblock/{uuid}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String unBlockProduct(@PathVariable UUID uuid)
    {
        productService.unblockProduct(uuid);
        return  "redirect:/product/home";
    }


}


