package com.example.StatMoney.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "soldAssets")
public class SoldAsset extends Asset{
    private double sellPriceRub;
    private double sellPriceUsd;
    private int sellQuantity;
    private Date sellDate;
    private double profitLossRub;
    private double profitLossUsd;
}
