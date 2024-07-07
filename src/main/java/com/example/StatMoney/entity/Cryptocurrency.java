package com.example.StatMoney.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "cryptocurrencies")
public class Cryptocurrency extends Asset{
    private String ticker;
}
