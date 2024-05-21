package com.example.AbsaFinancialSystem.SubcatConfig;

import com.sun.jna.platform.win32.Advapi32Util;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubclassRepository extends JpaRepository<SubCategory, Long> {

    @Query(value = "SELECT * FROM sub_category WHERE subcategory = ?1", nativeQuery = true)
    Optional<SubCategory> findBySubcategory(String subcategory);
    @Query(value = "SELECT subcategory FROM sub_category WHERE subsidiary_id = :subId and account_type = :accT" , nativeQuery = true)
    List<String> findAllAccountSubCategories(@Param("subId") int subId, @Param("accT") String accountType);

    @Query(value = "SELECT distinct account_type FROM sub_category WHERE subsidiary_id =:subId and category = :cat", nativeQuery = true)
    List<String> findAllSubsidiaryCategories(@Param("subId") int subId, @Param("cat") String cat);

    @Query(value = "SELECT  distinct  category FROM sub_category WHERE subsidiary_id =:subId", nativeQuery = true)
    List<String> findAllAccountCategories(@Param("subId") int subId);
    @Query(value = "SELECT id FROM sub_category WHERE subcategory =:subCat", nativeQuery = true)
    Long findAccountCategoryId(@Param("subCat") String subCat);
}
