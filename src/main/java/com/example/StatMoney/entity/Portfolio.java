package com.example.StatMoney.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "portfolios")
public class Portfolio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "idUser", referencedColumnName = "id")
    private User user;

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Asset> assets;

    private double totalValueRub;               // Общая стоимость в рублях
    private double totalValueUsd;               // Общая стоимость в долларах

    private double totalProfitLossRub;          // Общий доход/убыток в рублях
    private double totalProfitLossUsd;          // Общий доход/убыток в долларах

    private double averageDailyIncomeRub;       // Среднедневной доход в рублях
    private double averageDailyIncomeUsd;       // Среднедневной доход в долларах

    private double totalSoldAssetsValueRub;     // Общая стоимость проданных активов в рублях
    private double totalSoldAssetsValueUsd;     // Общая стоимость проданных активов в долларах
}
