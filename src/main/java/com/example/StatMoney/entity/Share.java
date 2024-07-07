package com.example.StatMoney.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="shares")
public class Share extends Asset{
    private String ticker;                 // Идентификационный номер ценной бумаги
}
