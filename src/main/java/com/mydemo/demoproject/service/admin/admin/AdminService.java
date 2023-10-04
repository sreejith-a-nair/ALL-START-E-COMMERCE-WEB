package com.mydemo.demoproject.service.admin.admin;

import com.mydemo.demoproject.Entity.UserEntity;
import com.mydemo.demoproject.Repository.admin.AdminRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AdminService {
    @Autowired
    AdminRepo adminRepo;

//    @Autowired
//    private PasswordEncoder passwordEncoder;

    public UserEntity save(UserEntity user) {
        return adminRepo.save(user);
    }

    public List<UserEntity> loadAllUsers() {
        return adminRepo.findAll();
    }

    public List<UserEntity> searchUsers(String keyword) {
        return adminRepo.findByKeyword(keyword);
    }


    public List<UserEntity> findAll() {
        return adminRepo.findByUsernameNot("admin");
    }

    /*pagination*/
     public Page<UserEntity>findPaginated(int pageNo, int pageSize){

         Pageable pageable= PageRequest.of(pageNo-1,pageSize);
         return this.adminRepo.findAll(pageable);
     }


    /*pagination end*/


    /*  block user*/
    public void blockUser(UUID uuid) {
        Optional<UserEntity> userdata = adminRepo.findById(uuid);
        if (userdata.isPresent()) {
            UserEntity user = userdata.get();
            user.setEnable(false);
            adminRepo.save(user);
        } else {
            throw new EntityNotFoundException("User not found with ID: " + uuid);
        }
    }

    /*  unblock user*/
    public void unblockUser(UUID uuid) {
        Optional<UserEntity> userdata = adminRepo.findById(uuid);
        if (userdata.isPresent()) {
            UserEntity user = userdata.get();
            user.setEnable(true);
            adminRepo.save(user);
        } else {
            throw new EntityNotFoundException("User not found with ID: " + uuid);
        }


    }


}



