package com.example.StatMoney.controller;

import com.example.StatMoney.Dto.ActiveForm;
import com.example.StatMoney.config.MyUserDetails;
import com.example.StatMoney.entity.Asset;
import com.example.StatMoney.entity.User;
import com.example.StatMoney.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class MainController {

    @Autowired
    private UserService userService;

    @Autowired
    MoexService moexService;

    @Autowired
    CryptoCompareService cryptoCompareService;

    @Autowired
    AssetService assetService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    String dashboard() {
        return "dashboard";
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    String actives(Model model) {
        return "stats";
    }

    @GetMapping("/add")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    String addActive(Model model, String type) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails myUserDetails = (MyUserDetails) auth.getPrincipal();
        User user = myUserDetails.getUser();
        model.addAttribute("user", user);

        if (type != null) {
            switch (type) {
                case "share":
                    System.out.println("Share");
                    model.addAttribute("shares", moexService.getAllSecurities("shares"));
                    return "add-share";
                case "bond":
                    System.out.println("Bond");
                    model.addAttribute("bonds", moexService.getAllSecurities("bonds"));
                    return "add-bond";
                case "crypt":
                    System.out.println("Crypt");
                    model.addAttribute("crypts", cryptoCompareService.getAllCryptocurrencies());
                    return "add-crypt";
                default:
                    break;
            }
        } else {

        }
        //List<Map<String, String>> qwe = moexService.getAllSecurities("shares");
        //System.out.println(cryptoCompareService.getAllCryptocurrencies());
        //System.out.println(moexService.getAllSecurities("shares"));
        //System.out.println(moexService.getAllSecurities("bonds"));
        System.out.println(type);
        return "add-active";
    }

    @PostMapping("/add")
    String getActive(@RequestParam String name, String quantity, String price)
    {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails myUserDetails = (MyUserDetails) auth.getPrincipal();
        User user = myUserDetails.getUser();
        assetService.addAsset(new Asset(null, ), user);
        System.out.println(name + " " + quantity + " " + price + " Price FROM POST!");

        return "redirect:/portfolio";
    }

}