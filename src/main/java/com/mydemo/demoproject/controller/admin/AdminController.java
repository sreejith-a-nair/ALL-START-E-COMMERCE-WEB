package com.mydemo.demoproject.controller.admin;

import com.mydemo.demoproject.Entity.UserEntity;
import com.mydemo.demoproject.service.admin.admin.AdminService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
public class AdminController {


    @Autowired
    AdminService adminService;


      /*home old*/
//    @GetMapping("/home")
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
//    public String adminHome(Model model){
////        System.out.println("hai admin//////////////////////////////////////////");
//        List<UserEntity> users = adminService.findAll();
////        System.out.println("search list" + users);
//
//
//        model.addAttribute("users", users);
//        return "admin/users";
//    }


    /*pagination*/
       /*new home*/
    @GetMapping("/home")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String adminHome(Model model){
      return   findPaginated(1,model);
    }


/*pagination*/

@GetMapping("/page/{pageNo}")
public String findPaginated(@PathVariable(value = "pageNo") int pageNo,Model model){
    int pageSize=5;
    Page<UserEntity>page=adminService.findPaginated(pageNo,pageSize);
    List<UserEntity>users=page.getContent();
    System.out.println("users in paginationppppp"+users);
    System.out.println("page in paginationppppp"+page);

    model.addAttribute("currentPage", pageNo);
    model.addAttribute("totalPages", page.getTotalPages());
    model.addAttribute("totalItems", page.getTotalElements());
    model.addAttribute("users", users);

    return "admin/users";
}

/*end pagination*/

    /*search user*/
    @GetMapping(value = "/search",params = "keyword")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String searchUsers(Model model, @Param("keyword") String keyword){
        try{
        List<UserEntity> users;
        if(keyword != null && !keyword.isEmpty()){
            users = adminService.searchUsers(keyword);
        }
        else{
            users = adminService.loadAllUsers();
        }


        model.addAttribute("users",users);

        return "admin/users";

    }catch (Exception e){

            e.printStackTrace();

            model.addAttribute("errorMessage", "An error occurred while processing your request.");
            return "error";
        }
    }


   /*   block  user /unblock user  */
   @GetMapping("/block/{uuid}")
   @PreAuthorize("hasAuthority('ROLE_ADMIN')")
   public String blockUser(@PathVariable UUID uuid)
    {
             adminService.blockUser(uuid);
            return "redirect:/admin/home";
   }


     /*Un-block user*/
   @GetMapping("/unblock/{uuid}")
   @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String unBlockUser(@PathVariable UUID uuid)
   {
       adminService.unblockUser(uuid);
        return  "redirect:/admin/home";
   }

/*    logout*/
    @GetMapping("/logout")
    public String handleLogoutRequest(HttpServletRequest request) {

        request.getSession().invalidate();
        return "redirect:/login?logout";
    }



    @GetMapping("/dashboard")
    public  String getDashboard()
    {
        return "admin/dashboard";
    }



}
