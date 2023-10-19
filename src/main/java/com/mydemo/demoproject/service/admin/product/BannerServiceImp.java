package com.mydemo.demoproject.service.admin.product;

import com.mydemo.demoproject.Entity.Banner;
import com.mydemo.demoproject.Repository.admin.BannerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BannerServiceImp implements  BannerService{


    @Autowired
    BannerRepo bannerRepo;
    @Override
    public Banner save(Banner banner) {
        return bannerRepo.save(banner);
    }

    @Override
    public List<Banner> findByAllBanner() {
        return bannerRepo.findAll();
    }


    @Override
    public void enableBanner(UUID uuid) {
        Optional<Banner> bannerInfo =bannerRepo.findById(uuid);
        if (bannerInfo.isPresent()) {

            Banner banner = bannerInfo.get();
            banner.setEnable(true);
            bannerRepo.save(banner);
        } else {
            throw new EntityNotFoundException("User not found with ID: " + uuid);
        }
    }

    @Override
    public void disableBanner(UUID uuid) {
        Optional<Banner> bannerInfo =bannerRepo.findById(uuid);
        if (bannerInfo.isPresent()) {
            Banner banner = bannerInfo.get();
            banner.setEnable(false);
            bannerRepo.save(banner);
        } else {
            throw new EntityNotFoundException("User not found with ID: " + uuid);
        }
    }


    @Override
    public Optional<Banner> findBannerById(UUID uuid) {
        return bannerRepo.findById(uuid);
    }

    @Override
    public List<String> getAllOriginalFileNames() {
        List<String> fileNames = new ArrayList<>();
        List<Banner> banners = bannerRepo.findAll();
        for (Banner banner : banners) {
            fileNames.add(banner.getFileName());
        }
        return fileNames;
    }


    @Override
    public boolean isOriginalFileNameDuplicate(String newFileName) {

        List<String> existingFileNames = getAllOriginalFileNames();

        for (String existingFileName : existingFileNames) {
            // Split the existing file name by hyphen and get the last part
            String[] parts = existingFileName.split("-");
            if (parts.length > 1) {
                String lastPart = parts[parts.length - 1].trim();
                if (newFileName.equals(lastPart)) {
                    return true;
                }
            }
        }

        return existingFileNames.contains(newFileName);
    }

    @Override
    public Page<Banner> findPaginated(int pageNo, int pageSize) {
        Pageable pageable= PageRequest.of(pageNo-1,pageSize);
        return this.bannerRepo.findAll(pageable);
    }

    public Optional<Banner> findBannerByFileName(String fileName) {

//        bannerRepo.findByFileName(fileName);
        return bannerRepo.findByFileName(fileName);
    }
}
