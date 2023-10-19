package com.mydemo.demoproject.service.admin.product;

import com.mydemo.demoproject.Entity.Banner;
import com.mydemo.demoproject.Entity.ProductInfo;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BannerService {

    Banner save(Banner banner);
    List<Banner> findByAllBanner();

    void enableBanner(UUID uuid);

    void disableBanner(UUID uuid);
    Optional<Banner> findBannerById(UUID uuid);

    /*new*/
     List<String> getAllOriginalFileNames();

    boolean isOriginalFileNameDuplicate(String newFileName);

    Page<Banner> findPaginated(int pageNo, int pageSize);


}
