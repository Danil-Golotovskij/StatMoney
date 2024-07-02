package com.example.StatMoney;

import lombok.Data;

@Data
public class AggregatedAsset {
    private String ticker;
    private String name;
    private String category;
    private float averagePurchasePriceRub;
    private float averagePurchasePriceUsd;
    private int totalQuantity;
    private float currentPriceRub;
    private float currentPriceUsd;

    // Lombok будет автоматически генерировать геттеры и сеттеры
}
