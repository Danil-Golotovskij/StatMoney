package com.example.StatMoney.controller;

import com.example.StatMoney.config.MyUserDetails;
import com.example.StatMoney.AggregatedAsset;
import com.example.StatMoney.entity.*;
import com.example.StatMoney.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class PortfolioController {
    private final AssetService assetService;
    private final PortfolioService portfolioService;
    private final MoexService moexService;
    private final CryptoCompareService cryptoCompareService;
    private final CbrService cbrService;

    @Autowired
    public PortfolioController(AssetService assetService, PortfolioService portfolioService, MoexService moexService,
                               CryptoCompareService cryptoCompareService, CbrService cbrService) {
        this.assetService = assetService;
        this.portfolioService = portfolioService;
        this.moexService = moexService;
        this.cryptoCompareService = cryptoCompareService;
        this.cbrService = cbrService;
    }

    @GetMapping("/portfolio")
    public String getAssetsPage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails myUserDetails = (MyUserDetails) auth.getPrincipal();
        User user = myUserDetails.getUser();

        Optional<Portfolio> portfolioOpt = portfolioService.findByUser(user);
        if (portfolioOpt.isPresent()) {
            Portfolio portfolio = portfolioOpt.get();
            List<Asset> assets = assetService.findByPortfolioId(portfolio.getId());
            float usdToRubRate = cbrService.getCurrentCurrencyRate("USD");

            Map<String, AggregatedAsset> aggregatedAssetsMap = new HashMap<>();
            float investedSumRub = 0;  //Потраченная сумма в RUB
            float currentPortfolioValueRub = 0;  //Текущая стоимость портфеля в RUB
            float investedSumUsd = 0;  //Потраченная сумма в USD
            float currentPortfolioValueUsd = 0;  //Текущая стоимость портфеля в USD

            for (Asset asset : assets) {
                String ticker = getTickerOfAsset(asset);
                AggregatedAsset aggregatedAsset = aggregatedAssetsMap.getOrDefault(ticker, new AggregatedAsset());
                aggregatedAsset.setTicker(ticker);
                aggregatedAsset.setName(asset.getName());
                aggregatedAsset.setCategory(getRussianCategory(asset.getCategory()));
                aggregatedAsset.setTotalQuantity(aggregatedAsset.getTotalQuantity() + asset.getQuantity());
                aggregatedAsset.setAveragePurchasePriceRub(
                        (float) ((aggregatedAsset.getAveragePurchasePriceRub() *
                                (aggregatedAsset.getTotalQuantity() - asset.getQuantity()) +
                                asset.getPurchasePriceRub() * asset.getQuantity()) /
                                aggregatedAsset.getTotalQuantity()));
                aggregatedAsset.setAveragePurchasePriceUsd(
                        (float) ((aggregatedAsset.getAveragePurchasePriceUsd() * (aggregatedAsset.getTotalQuantity() -
                                asset.getQuantity()) + asset.getPurchasePriceUsd() * asset.getQuantity()) /
                                aggregatedAsset.getTotalQuantity()));

                //Получаем текущую цену актива
                float currentPriceRub = getCurrentAssetPrice(asset);
                aggregatedAsset.setCurrentPriceRub(currentPriceRub);
                aggregatedAsset.setCurrentPriceUsd(currentPriceRub / usdToRubRate);

                aggregatedAssetsMap.put(ticker, aggregatedAsset);

                //Рассчитываем потраченную сумму
                investedSumRub += (float) (asset.getPurchasePriceRub() * asset.getQuantity());
                investedSumUsd += (float) (asset.getPurchasePriceUsd() * asset.getQuantity());
                //Рассчитываем текущую стоимость портфеля
                currentPortfolioValueRub += currentPriceRub * asset.getQuantity();
                currentPortfolioValueUsd += (currentPriceRub / usdToRubRate) * asset.getQuantity();
            }

            Map<String, Float> profitRub = new HashMap<>();
            Map<String, Float> profitUsd = new HashMap<>();

            for (AggregatedAsset asset : aggregatedAssetsMap.values()) {
                float profitRubPercent = ((asset.getCurrentPriceRub() - asset.getAveragePurchasePriceRub()) /
                        asset.getAveragePurchasePriceRub()) * 100;
                float profitUsdPercent = ((asset.getCurrentPriceUsd() - asset.getAveragePurchasePriceUsd()) /
                        asset.getAveragePurchasePriceUsd()) * 100;

                profitRub.put(asset.getTicker(), profitRubPercent);
                profitUsd.put(asset.getTicker(), profitUsdPercent);
            }

            //Расчет разницы в процентах
            float portfolioChangePercentRub = ((currentPortfolioValueRub - investedSumRub) / investedSumRub) * 100;
            float portfolioChangePercentUsd = ((currentPortfolioValueUsd - investedSumUsd) / investedSumUsd) * 100;

            model.addAttribute("assets", aggregatedAssetsMap.values());
            model.addAttribute("profitRub", profitRub);
            model.addAttribute("profitUsd", profitUsd);
            model.addAttribute("totalValueRub", currentPortfolioValueRub);
            model.addAttribute("totalValueUsd", currentPortfolioValueUsd);
            model.addAttribute("portfolioChangePercentRub", portfolioChangePercentRub);
            model.addAttribute("portfolioChangePercentUsd", portfolioChangePercentUsd);

        } else {
            model.addAttribute("message", "No portfolio found for user");
        }

        return "portfolio";
    }

    @GetMapping("/portfolio/add")
    public String showAddAssetForm(Model model) {
        model.addAttribute("asset", new Asset());
        return "add-asset";
    }

    @PostMapping("/portfolio/add")
    public String addAsset(@ModelAttribute Asset asset) {
        assetService.addAsset(asset);
        return "redirect:/portfolio";
    }

    @PostMapping("/portfolio/delete/{name}")
    public String deleteAssetsByName(@PathVariable String name) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails myUserDetails = (MyUserDetails) auth.getPrincipal();
        User user = myUserDetails.getUser();
        Optional<Portfolio> portfolioOpt = portfolioService.findByUser(user);
        portfolioOpt.ifPresent(portfolio -> assetService.deleteAssetsByPortfolioIdAndName(portfolio.getId(), name));
        return "redirect:/portfolio";
    }

    private String getTickerOfAsset(Asset asset) {
        return switch (asset) {
            case Bond bond -> bond.getTicker();
            case Cryptocurrency cryptocurrency -> cryptocurrency.getTicker();
            case Share share -> share.getTicker();
            case null, default -> "N/A";
        };
    }

    private String getRussianCategory(String category) {
        return switch (category) {
            case "Bond" -> "Облигация";
            case "Cryptocurrency" -> "Криптовалюта";
            case "Share" -> "Акция";
            default -> category;
        };
    }

    private float getCurrentAssetPrice(Asset asset) {
        return switch (asset) {
            case Bond bond -> moexService.getCurrentBondPrice(bond.getTicker());
            case Cryptocurrency cryptocurrency -> cryptoCompareService.getCryptoPrice(cryptocurrency.getTicker());
            case Share share -> moexService.getCurrentPrice(share.getTicker());
            case null, default -> 0;
        };
    }
}
