package com.example.AbsaFinancialSystem.Reports;

import net.sf.jasperreports.engine.JRException;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;

@RestController
@RequestMapping("/reports")
public class Controller {

    private Service service;
    @GetMapping("/jasper")
    public ResponseEntity<T>generatereport() throws JRException, FileNotFoundException {
         String response=  service.exportReport();
         return  new ResponseEntity<>(HttpStatus.OK);
    }
}
