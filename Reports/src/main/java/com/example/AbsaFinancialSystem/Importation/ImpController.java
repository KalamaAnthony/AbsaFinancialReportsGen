package com.example.AbsaFinancialSystem.Importation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController





public class ImpController {

    @Autowired
    ImpService impService= new ImpService();

    @PostMapping(value = "/import",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)

    public ResponseEntity<List<Imp>> uploadFile(@RequestPart("file") MultipartFile file) {
        try {
            List<Imp> records = impService.loadExcelFile(file);
            return ResponseEntity.ok(records);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);
        }

    }
}