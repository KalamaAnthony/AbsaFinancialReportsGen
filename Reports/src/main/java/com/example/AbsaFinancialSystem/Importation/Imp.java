package com.example.AbsaFinancialSystem.Importation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Imp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String period;
    private Long account;
    private String accountDescription;
    private String plOrBs;
    private String subCategory;
    private BigDecimal net;

    public static BigDecimal convertNetValue(String clientValue) {
        if (clientValue.startsWith("(") && clientValue.endsWith(")")) {
            return new BigDecimal(clientValue.substring(1, clientValue.length() - 1)).negate();
        } else {
            return new BigDecimal(clientValue);
        }
    }
}
