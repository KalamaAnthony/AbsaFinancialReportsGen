package com.example.AbsaFinancialSystem.Importation;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class BalanceSheetCat implements Serializable {
    private String title;
    private List<BalanceSheetAccCategory> accCategories;
}
