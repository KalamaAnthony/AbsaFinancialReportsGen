package com.example.AbsaFinancialSystem.LedgerComponent;

import com.example.AbsaFinancialSystem.Utilities.EntityResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(path = "api/v1/ledger")
public class LedgerController {
    @Autowired
     LedgerService ledgerService;




    @PostMapping("/create")
    public EntityResponse addLedger (@RequestBody List<Ledger> ledgerAccountList){
   return ledgerService.addLedger(ledgerAccountList);

    }

    @GetMapping("/find/all/{in ledger}")
    public EntityResponse findAll() {
        return ledgerService.findAll();

}
}
