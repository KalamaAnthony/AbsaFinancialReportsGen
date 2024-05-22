package com.example.AbsaFinancialSystem.Importation;

import com.example.AbsaFinancialSystem.SubcatConfig.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface ImpRepo extends JpaRepository<Imp, Long> {

    @Query(value = "SELECT SUM(net) FROM imp WHERE subcategory_id= :categoryTypeId GROUP BY subcategory_id", nativeQuery = true)
    Long findSumOfSubcategories(@Param("categoryTypeId") long categoryTypeId);
    @Query(value = "SELECT net FROM imp WHERE subcategory_id= :categoryTypeId", nativeQuery = true)
    Long findNetOfSubcategories(@Param("categoryTypeId") long categoryTypeId);

}

