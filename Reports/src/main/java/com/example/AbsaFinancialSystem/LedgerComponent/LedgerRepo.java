package com.example.AbsaFinancialSystem.LedgerComponent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LedgerRepo extends JpaRepository<Ledger , Long> {

}
