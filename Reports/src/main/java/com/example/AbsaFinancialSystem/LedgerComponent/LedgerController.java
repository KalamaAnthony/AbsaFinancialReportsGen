package com.example.AbsaFinancialSystem.LedgerComponent;

import com.example.AbsaFinancialSystem.Utilities.EntityResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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




    @PostMapping("/create" )
    public EntityResponse addLedger (@RequestBody List<Ledger> ledgerAccountList){
   return ledgerService.addLedger(ledgerAccountList);

    }

    @GetMapping("/find/all/{in ledger}")
    public EntityResponse findAll() {
        return ledgerService.findAll();

}
    @PostMapping(value = "/upload/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EntityResponse<?>> uploadFile(@RequestPart("files") MultipartFile files) {
        try {
            EntityResponse<?> response = ledgerService.uploadFile(files);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
//    @GetMapping("/balance-sheet")
//    public Ledger generateBalanceSheet() {
//        return ledgerService.generateBalanceSheet();
//    }

}