package com.mydemo.demoproject.service.home;

import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

public class HomeService {
    @GetMapping("/logout")
    public String handleLogoutRequest(HttpServletRequest request) {

        request.getSession().invalidate();
        return "redirect:/login?logout";
    }

}
