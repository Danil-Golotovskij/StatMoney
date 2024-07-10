package com.example.StatMoney.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Entity
@Table(name = "bonds")
@NoArgsConstructor
public class Bond extends Asset{
    private String ticker;                 // Идентификационный номер ценной бумаги
}
