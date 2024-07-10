package com.example.StatMoney.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "portfolios")
public class Portfolio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "id_user", referencedColumnName = "id")
    private User user;

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Asset> assets = new ArrayList<>();

    private double totalValueRub;               // Общая стоимость в рублях
    private double totalValueUsd;               // Общая стоимость в долларах

    private double totalProfitLossRub;          // Общий доход/убыток в рублях
    private double totalProfitLossUsd;          // Общий доход/убыток в долларах

    private double averageDailyIncomeRub;       // Среднедневной доход в рублях
    private double averageDailyIncomeUsd;       // Среднедневной доход в долларах

    private double totalSoldAssetsValueRub;     // Общая стоимость проданных активов в рублях
    private double totalSoldAssetsValueUsd;     // Общая стоимость проданных активов в долларах

    public void addAsset(Asset asset) {
        assets.add(asset);
        asset.setPortfolio(this);
    }

    public void removeAsset(Asset asset) {
        assets.remove(asset);
        asset.setPortfolio(null);
    }
}
