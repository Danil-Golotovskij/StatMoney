package com.example.StatMoney.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name="assets")
@NoArgsConstructor
public class Asset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "id_portfolio")
    private Portfolio portfolio;
    private String name;
    private String category;
    private double purchasePriceRub;
    private double purchasePriceUsd;
    private double quantity;
}


