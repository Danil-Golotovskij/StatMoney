package com.example.StatMoney.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Entity
@Table(name = "cryptocurrencies")
@NoArgsConstructor
public class Cryptocurrency extends Asset{
    private String ticker;
}
