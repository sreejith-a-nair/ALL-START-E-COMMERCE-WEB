package com.mydemo.demoproject.controller.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mydemo.demoproject.Entity.Banner;
import com.mydemo.demoproject.Entity.ProductInfo;
import com.mydemo.demoproject.Repository.admin.BannerRepo;
import com.mydemo.demoproject.service.admin.product.BannerService;
import com.mydemo.demoproject.service.admin.product.ProductServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Controller
@RequestMapping("/banner")
public class BannerController {

    @Autowired
    BannerService bannerService;


    @GetMapping("/home")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String showAllProduct(Model model){
        return   findPaginated(1,model);
    }

    @GetMapping("/page/{pageNo}")
public String findPaginated(@PathVariable(value = "pageNo") int pageNo,Model model){
    int pageSize=3;
    Page<Banner> page=bannerService.findPaginated(pageNo,pageSize);
    List< Banner>bannerInfo=page.getContent();

    model.addAttribute("currentPage", pageNo);
    model.addAttribute("totalPages", page.getTotalPages());
    model.addAttribute("totalItems", page.getTotalElements());
    model.addAttribute("bannerInfo", bannerInfo);

    return "admin/banner";
}


@GetMapping("/add-banner")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public String addBanner(@RequestParam(value="error",required=false)String error,Model model )throws JsonProcessingException {
        if (error != null && error.equals("BannerExists")) {
            model.addAttribute("error", "Banner already exists.");
        }
    return "admin/add-banner";
}

    @PostMapping("/save-banner")
    public String addBanner(@RequestParam("fileName") List<MultipartFile> fileName,
                            @RequestParam("bannerName") String bannerName,
                            Model model) throws IOException {
        List<Banner> banners = new ArrayList<>();
        boolean hasDuplicate = false;

        if (!fileName.isEmpty() && !fileName.get(0).getOriginalFilename().equals("")) {


            for (MultipartFile image : fileName) {

                String newFileName = image.getOriginalFilename();

                if (bannerService.isOriginalFileNameDuplicate(newFileName)) {
                    model.addAttribute("error", "Duplicate file name found");
                    return "admin/add-banner";
                }
                String fileLocation = handleFileUploading(image);
                Banner bannerEnity = new Banner(fileLocation);
                bannerEnity.setBannerName(bannerName);
                bannerEnity = bannerService.save(bannerEnity);
                banners.add(bannerEnity);
            }
        }
        else {
            Banner banner = new Banner();
            banner.setFileName(banners.get(0).getFileName());
            bannerService.save(banner);
        }

        return "redirect:/banner/home";
    }

    /*THIS POST HAVE ERROR*/
    /*edit banner*/
    @GetMapping("/edit/{uuid}")
    public  String updateBanner(@PathVariable UUID uuid,
                                Model model){

        Optional<Banner> banner=  bannerService.findBannerById(uuid);
        model.addAttribute("banner",banner.orElse(new Banner()));
        return  "admin/edit-banner";

    }
    @PostMapping("/update")
    public String updateBanner(@ModelAttribute("banner") Banner banner,
                               @RequestParam("newImage") MultipartFile newImage,
                                @RequestParam("bannerName") String bannerName   ) throws IOException {
        UUID bannerId = banner.getUuid();
        if (bannerId != null) {
            Optional<Banner> existingBanner = bannerService.findBannerById(bannerId);

            if (existingBanner.isPresent()) {
                Banner bannerToUpdate = existingBanner.get();

                String newFileName = newImage.getOriginalFilename();

                if (!newFileName.equals(bannerToUpdate.getFileName())) {

                    String fileLocation = handleFileUploading(newImage);
                    bannerToUpdate.setFileName(fileLocation);

                    bannerToUpdate.setBannerName(bannerName);
                    bannerService.save(bannerToUpdate);

                    return "redirect:/banner/home";
                }
            }
        }
        return "redirect:/banner/home";
    }

    private String handleFileUploading(MultipartFile file) throws IOException {
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

    /*Disable Banner*/
    @GetMapping("/block/{uuid}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String blockBanner(@PathVariable UUID uuid)
    {
        bannerService.disableBanner(uuid);
        return "redirect:/banner/home";
    }


    /*Enable Banner*/
    @GetMapping("/unblock/{uuid}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String unBlockBanner(@PathVariable UUID uuid)
    {
        bannerService.enableBanner(uuid);
        return  "redirect:/banner/home";
    }

}
