package com.example.AbsaFinancialSystem.Reports;

import com.example.AbsaFinancialSystem.Importation.BalanceSheet;
import com.example.AbsaFinancialSystem.Importation.Imp;
import com.example.AbsaFinancialSystem.Importation.ImpController;
import com.example.AbsaFinancialSystem.Importation.ImpRepo;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@org.springframework.stereotype.Service
@Slf4j
public class Service {

    private ImpController impController;
    private ImpRepo imprepository;


    public String exportReport() throws FileNotFoundException, JRException {
        String path = "C:\\Users\\Arnold Kipkemboi\\JaspersoftWorkspace\\MyReports";
        List<Imp> employees = imprepository.findAll();

        File file = ResourceUtils.getFile("classpath:Balance.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
        JRBeanCollectionDataSource Deira = new JRBeanCollectionDataSource(employees);
        Map<String, Object> parameters = new HashMap<>();
//        parameters.put("createdBy", "Java");
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, Deira);
        JasperExportManager.exportReportToPdfFile(jasperPrint, path + "\\Balancesheet.pdf");


        return "report generated in path : " + path;

    }
}
