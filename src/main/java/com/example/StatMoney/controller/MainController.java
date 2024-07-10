package com.example.StatMoney.controller;

import com.example.StatMoney.Dto.ActiveForm;
import com.example.StatMoney.config.MyUserDetails;
import com.example.StatMoney.entity.*;
import com.example.StatMoney.repository.BondRepository;
import com.example.StatMoney.repository.CryptRepository;
import com.example.StatMoney.repository.ShareRepository;
import com.example.StatMoney.service.*;
import org.springframework.beans.BeanUtils;
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
    ShareRepository shareRepository;

    @Autowired
    BondRepository bondRepository;

    @Autowired
    CryptRepository cryptRepository;

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

        if (type != null) {
            switch (type) {
                case "share":
                    model.addAttribute("shares", moexService.getAllSecurities("shares"));
                    return "add-share";
                case "bond":
                    model.addAttribute("bonds", moexService.getAllSecurities("bonds"));
                    return "add-bond";
                case "crypt":
                    model.addAttribute("crypts", cryptoCompareService.getAllCryptocurrencies());
                    return "add-crypt";
                default:
                    break;
            }
        }
        return "add-active";
    }

    @PostMapping("/add")
    String getActive(@RequestParam String name, String type, String ticker, String quantity, String price) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails myUserDetails = (MyUserDetails) auth.getPrincipal();
        User user = myUserDetails.getUser();

        double dollar = cbrService.getCurrentCurrencyRate("USD");
        double purchasePriceUsd = Double.parseDouble(price) / dollar;

        Asset asset = new Asset(null, null, name, getAssetCategory(type), Double.parseDouble(price), purchasePriceUsd, Double.parseDouble(quantity));
        Asset savedAsset = assetService.addAsset(asset, user);

        switch (type) {
            case "share":
                Share share = new Share();
                BeanUtils.copyProperties(savedAsset, share);
                share.setTicker(ticker);
                shareRepository.save(share);
                break;
            case "bond":
                Bond bond = new Bond();
                BeanUtils.copyProperties(savedAsset, bond);
                bond.setTicker(ticker);
                bondRepository.save(bond);
                break;
            case "crypt":
                Cryptocurrency crypto = new Cryptocurrency();
                BeanUtils.copyProperties(savedAsset, crypto);
                crypto.setTicker(ticker);
                cryptRepository.save(crypto);
                break;
        }

        return "redirect:/portfolio";
    }


    private String getAssetCategory(String type) {
        switch (type) {
            case "share":
                return "Акция";
            case "bond":
                return "Облигация";
            case "crypt":
                return "Криптовалюта";
            default:
                return "Unknown";
        }
    }


}