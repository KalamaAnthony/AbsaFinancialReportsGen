package com.example.AbsaFinancialSystem.Importation;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class BalanceSheet implements Serializable {
    private String title;
    private List<BalanceSheetCat> categories;
}
