package com.example.AbsaFinancialSystem.LedgerComponent;
import com.example.AbsaFinancialSystem.SubsidiaryComponent.Subsidiary;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Ledger")
public class Ledger {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String LedgerCode;
    //private Long subsidiary_Id;
    private String AccountNo;
    private String AccountName;
    private String AccountDescription;
    private String AccountType;
    private String SubCategory;
    private Double net;




    @ManyToOne
    @JoinColumn(name = "subsidiary_id", referencedColumnName = "id")
    private Subsidiary subsidiary;

}
