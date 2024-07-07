package com.example.StatMoney.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name = "deposits")
public class Deposit extends Asset{
    private String bankName;
    private double initialAmountRub;      // Начальная сумма в рублях
    private double interestRate;          // Процентная ставка
    private Date startDate;
    private Date endDate;
}
