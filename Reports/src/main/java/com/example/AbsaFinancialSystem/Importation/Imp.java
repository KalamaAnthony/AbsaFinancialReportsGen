package com.example.AbsaFinancialSystem.Importation;

import com.example.AbsaFinancialSystem.SubcatConfig.SubCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "imp")
public class Imp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String period;
    private Long account;
    private String accountDescription;
    @ManyToOne
    private SubCategory subcategory;


    private BigDecimal net;

    public static BigDecimal convertNetValue(String clientValue) {
        if (clientValue.startsWith("(") && clientValue.endsWith(")")) {
            return new BigDecimal(clientValue.substring(1, clientValue.length() - 1)).negate();
        } else {
            return new BigDecimal(clientValue);
        }
    }
}
