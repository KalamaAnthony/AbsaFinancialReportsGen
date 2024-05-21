package com.example.AbsaFinancialSystem.Importation;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class BalanceSheetAccCategory implements Serializable {
    private String title;
    private double sum;
}
