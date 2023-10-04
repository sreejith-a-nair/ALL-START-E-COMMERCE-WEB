package com.mydemo.demoproject.service.admin.product;

import com.mydemo.demoproject.Entity.Banner;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BannerService {
    Banner save(Banner banner);
    List<Banner> findByAllBanner();

//    public List<MultipartFile> findByAllBanners();



    void enableBanner(UUID uuid);

    void disableBanner(UUID uuid);
    Optional<Banner> findBannerById(UUID uuid);


    /*new*/
    public List<String> getAllOriginalFileNames();
    boolean isOriginalFileNameDuplicate(String newFileName);
}
