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
    CbrService cbrService;

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
                    //System.out.println(moexService.getAllSecurities("bonds"));
                    model.addAttribute("bonds", moexService.getAllSecurities("bonds"));
                    return "add-bond";
                case "crypt":
                    System.out.println("Crypt");
                    model.addAttribute("crypts", cryptoCompareService.getAllCryptocurrencies());
                    return "add-crypt";
                default:
                    break;
            }
        }
        return "add-active";
    }

    @PostMapping("/add")
    String getActive(@RequestParam String name, String type, String quantity, String price) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails myUserDetails = (MyUserDetails) auth.getPrincipal();
        User user = myUserDetails.getUser();

        double dollar = cbrService.getCurrentCurrencyRate("USD");

        switch (type) {
            case "share":
                assetService.addAsset(new Asset(null, null, name, "Акция", Double.parseDouble(price),
                        Double.parseDouble(price) * dollar, Double.parseDouble(quantity)), user);
                break;
            case "bond":
                assetService.addAsset(new Asset(null, null, name, "Облигация", Double.parseDouble(price),
                        Double.parseDouble(price) * dollar, Double.parseDouble(quantity)), user);
                break;
            case "crypt":
                assetService.addAsset(new Asset(null, null, name, "Криптовалюта", Double.parseDouble(price),
                        Double.parseDouble(price) * dollar, Double.parseDouble(quantity)), user);
                break;
        }

        System.out.println(name + " " + type + " " + quantity + " " + price + " FROM POST!");

        return "redirect:/portfolio";
    }

}