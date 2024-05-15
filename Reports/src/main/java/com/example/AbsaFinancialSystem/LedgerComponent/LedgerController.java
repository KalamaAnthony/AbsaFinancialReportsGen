package com.example.AbsaFinancialSystem.LedgerComponent;

import com.example.AbsaFinancialSystem.Utilities.EntityResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping(path = "api/v1/ledger")
@CrossOrigin
public class LedgerController {
    @Autowired
     LedgerService ledgerService;
    @Autowired
    LedgerRepo ledgerRepo;




    @PostMapping("/create")
    public EntityResponse addLedger (@RequestBody List<Ledger> ledgerAccountList){
   return ledgerService.addLedger(ledgerAccountList);

    }

    @GetMapping("/find/all/{in ledger}")
    public EntityResponse findAll() {
        return ledgerService.findAll();

}
//    @GetMapping("/balance-sheet")
//    public Ledger generateBalanceSheet() {
//        return ledgerService.generateBalanceSheet();
//    }

}