package com.example.moneycard.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "moneycards")
@Data
@NoArgsConstructor
public class MoneyCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double amount;

    private String owner;

//    public Long getId() {
//        return id;
//    }
}
