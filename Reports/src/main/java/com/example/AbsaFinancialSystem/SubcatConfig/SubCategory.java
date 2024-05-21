package com.example.AbsaFinancialSystem.SubcatConfig;


import com.example.AbsaFinancialSystem.Importation.Imp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import javax.persistence.*;
import java.util.List;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="sub_category")
public class SubCategory {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;
    private  String subcategory;

    @Enumerated(EnumType.STRING)
    private SubCatEnum accountType;

    @Enumerated(EnumType.STRING)
    private Enumsubclasses enumsubclasses;

    @Enumerated(EnumType.STRING)
    private CategoryEnum category;

//    @OneToMany
//    private List<Imp> imp;




    private  int subsidiaryId;


}

