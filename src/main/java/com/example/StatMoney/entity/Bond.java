package com.example.StatMoney.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "bonds")
public class Bond extends Asset{
    private String ticker;                 // Идентификационный номер ценной бумаги
}
