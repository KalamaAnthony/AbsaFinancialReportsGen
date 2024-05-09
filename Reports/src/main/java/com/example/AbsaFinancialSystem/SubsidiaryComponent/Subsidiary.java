package com.example.AbsaFinancialSystem.SubsidiaryComponent;

//import jakarta.persistence.*;
import com.example.AbsaFinancialSystem.LedgerComponent.Ledger;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Subsidiary")
public class Subsidiary {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private String subsidiaryCode;
    private String subsidiaryName;
    private LocalDate createdOn;



    @OneToMany(mappedBy = "subsidiary", cascade = CascadeType.ALL)
    private List<Ledger> ledgers;

}
