package com.example.StatMoney.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Entity
@Table(name="shares")
@NoArgsConstructor
public class Share extends Asset{
    private String ticker;                 // Идентификационный номер ценной бумаги
}
