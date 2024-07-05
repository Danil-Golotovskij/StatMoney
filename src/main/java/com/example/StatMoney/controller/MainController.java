package com.example.StatMoney.controller;

import com.example.StatMoney.config.MyUserDetails;
import com.example.StatMoney.entity.User;
import com.example.StatMoney.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    String dashboard()
    {
        return "dashboard";
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    String actives(Model model)
    {
        return "stats";
    }

    @GetMapping("/add")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    String addActive(Model model)
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails myUserDetails = (MyUserDetails) auth.getPrincipal();
        User user = myUserDetails.getUser();
        model.addAttribute("user", user);
        return "add-active";
    }

}
