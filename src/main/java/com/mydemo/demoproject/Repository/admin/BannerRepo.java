package com.mydemo.demoproject.Repository.admin;

import com.mydemo.demoproject.Entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BannerRepo extends JpaRepository<Banner, UUID> {


    Optional<Banner> findByFileName(String fileName);
}
