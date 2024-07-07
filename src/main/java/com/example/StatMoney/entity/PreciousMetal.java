package com.example.StatMoney.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "preciousMetals")
public class PreciousMetal extends Asset{
    private String metalType;
    private double quantityGr;
    private Date purchaseDate;
}
