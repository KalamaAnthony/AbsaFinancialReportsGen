package com.example.AbsaFinancialSystem.SubsidiaryComponent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubsidiaryRepo extends JpaRepository <Subsidiary,Long> {
}
