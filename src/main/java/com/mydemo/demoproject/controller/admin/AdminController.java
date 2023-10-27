package com.mydemo.demoproject.controller.admin;

import com.mydemo.demoproject.Entity.UserEntity;
import com.mydemo.demoproject.service.admin.admin.AdminService;

import com.mydemo.demoproject.service.admin.brand.BrandService;
import com.mydemo.demoproject.service.admin.cartegory.CategoryService;
import com.mydemo.demoproject.service.admin.order.OrderService;
import com.mydemo.demoproject.service.admin.product.ProductService;
import com.mydemo.demoproject.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
public class AdminController {


    @Autowired
    AdminService adminService;

    @Autowired
    UserService userService;

    @Autowired
    OrderService orderService;

    @Autowired
    ProductService productService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    BrandService brandService;


    /*pagination*/
       /*new home*/
    @GetMapping("/home")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String adminHome(Model model){
        System.out.println("hello sreejith");
      return   findPaginated(1,model);
    }


/*pagination*/

@GetMapping("/page/{pageNo}")
public String findPaginated(@PathVariable(value = "pageNo") int pageNo,Model model){

    int pageSize=5;
    Page<UserEntity>page=adminService.findPaginated(pageNo,pageSize);
    List<UserEntity>users=page.getContent();

    model.addAttribute("currentPage", pageNo);
    model.addAttribute("totalPages", page.getTotalPages());
    model.addAttribute("totalItems", page.getTotalElements());
    model.addAttribute("users", users);
    return "admin/users";
}
/*search.....*/
    @GetMapping("/search")
    public String searchUsers(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "page", defaultValue = "1") int pageNo,
            Model model) {
        int pageSize = 3;

        Page<UserEntity> users = userService.searchUsers(keyword, pageNo, pageSize);

        model.addAttribute("users", users.getContent());
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", users.getTotalPages());
        model.addAttribute("totalItems", users.getTotalElements());

        return "admin/users";
    }

/*end pagination*/


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
    public  String getDashboard(Model model)
    {

        /*new update*/
        int totalCustomer =userService.totalCustomers();

        int totalOrder=orderService.totalOrders();

        Long totalOrderPrice =orderService.totalOrderPrice();

        int totalProducts =productService.findProductCount();
        int totalBrands=brandService.findBrandCount();
        int totalCategory=categoryService.findCategoryCount();

        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(1);
        LocalDateTime monthlyStartDate = endDate.minusMonths(1);
        LocalDateTime weeklyStartDate=endDate.minusDays(7);

        int dailySale=orderService.getDailyTotalSale(startDate,endDate);
        int monthlySale=orderService.getMonthlyTotalSale(monthlyStartDate,endDate);
        int weeklySale=orderService.getWeeklyTotalSale(weeklyStartDate,endDate);

        int deliveredCount=orderService.getDeliveredCount();
        int canceledCount=orderService.getCanceledCount();
        int returnedCount=orderService.getReturnedCount();
        int shippedCount=orderService.getShippedCount();



        model.addAttribute("deliveredCount",deliveredCount);
        model.addAttribute("canceledCount",canceledCount);
        model.addAttribute("returnedCount",returnedCount);
        model.addAttribute("shippedCount",shippedCount);
        model.addAttribute("dailySale",dailySale);
        model.addAttribute("monthlySale",monthlySale);
        model.addAttribute("weeklySale",weeklySale);
        model.addAttribute("totalBrands",totalBrands);
        model.addAttribute("totalCategory",totalCategory);
        model.addAttribute("totalOrderPrice", totalOrderPrice);
        model.addAttribute("totalProducts",totalProducts);
        model.addAttribute("totalCustomer",totalCustomer);
        model.addAttribute("totalOrder",totalOrder);

        return "admin/dashboard";
    }


}
