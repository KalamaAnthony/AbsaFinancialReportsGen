package com.example.AbsaFinancialSystem.LedgerComponent;

import com.example.AbsaFinancialSystem.Utilities.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LedgerService {

    @Autowired
    LedgerRepo ledgerRepo;

    public EntityResponse addLedger(List<Ledger> ledgerAccountList) {
        EntityResponse response = new EntityResponse<>();
        try {
            List<Ledger> ledgers = ledgerRepo.saveAll(ledgerAccountList);
            response.setMessage("Ledger accounts added successfully");
            response.setStatusCode(HttpStatus.CREATED.value());
            response.setEntity(ledgers);


        } catch (Exception e) {
            log.error("error {}", e);
        }
        return response;
    }
    private List<Ledger> readExcelFile(MultipartFile file) throws IOException {
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        List<Ledger> ledgers = new ArrayList<>();

        try {
            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue;
                }
                Ledger ledger1 = new Ledger();

                ledger1.setAccountNo(getStringValue(row.getCell(0)));
                ledger1.setAccountDescription(getStringValue(row.getCell(1)));
                ledger1.setAccountType(getStringValue(row.getCell(2)));
                ledger1.setSubCategory(getStringValue(row.getCell(3)));
                ledger1.setNet(Double.parseDouble(getStringValue(row.getCell(4))));

                //parse the date


                ledgers.add(ledger1);
            }
        } finally {
            workbook.close();
        }
        return ledgers;
    }

    private String getStringValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case BLANK:
                return "";
            default:
                return null;

    }}
    public EntityResponse uploadFile(MultipartFile files) {
        EntityResponse response = new EntityResponse<>();
        try {
            List<Ledger> accounts = readExcelFile(files);
            response.setEntity(accounts);
            response.setMessage("successfully converted");
            ledgerRepo.saveAll(accounts);
        } catch (Exception e) {
            log.error("Failed to save Transactions", e);
            response.setMessage("Failed to save Transactions");
            response.setEntity(null);
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

        }
        return response;
    }

    //   @PostMapping("/upload")
//    public ResponseEntity<String> uploadExcelFile(@RequestParam("file") MultipartFile file) {
//        try {
//            Workbook workbook = new XSSFWorkbook(file.getInputStream()); // Assuming it's an XLSX file
//            Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet
//
//            // Process each row in the sheet
//            for (Row row : sheet) {
//                // Assuming the data is in specific columns
//                String accountName = row.getCell(0).getStringCellValue();
//                String accountDescription = row.getCell(1).getStringCellValue();
//                // Extract other fields similarly
//
//                // Create ledger object and save to database
//                Ledger ledger = new Ledger();
//                ledger.setAccountName(accountName);
//                ledger.setAccountDescription(accountDescription);
//                // Set other fields
//
//                ledgerService.addLedger(ledger);
//            }
//
//            workbook.close();
//            return ResponseEntity.ok("File uploaded successfully");
//        } catch (IOException e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading file");
//        }
    //   }
    public EntityResponse<List<Map<String, Object>>> findAll() {
        EntityResponse<List<Map<String, Object>>> response = new EntityResponse<>();
        try {
            List<Ledger> ledgers = ledgerRepo.findAll();
            if (!ledgers.isEmpty()) {
                response.setMessage("Ledgers retrieved successfully");
                response.setStatusCode(HttpStatus.FOUND.value());
                List<Map<String, Object>> ledgersResponse = ledgers.stream().map(ledger -> {
                    Map<String, Object> ledgerMap = new HashMap<>();
                    ledgerMap.put("id", ledger.getId());
                    ledgerMap.put("ledgerCode", ledger.getLedgerCode());
                    ledgerMap.put("accountNo", ledger.getAccountNo());
                    ledgerMap.put("accountName", ledger.getAccountName());
                    ledgerMap.put("accountDescription", ledger.getAccountDescription());
                    ledgerMap.put("accountType", ledger.getAccountType());
                    ledgerMap.put("subCategory", ledger.getSubCategory());
                    ledgerMap.put("net", ledger.getNet());
                    ledgerMap.put("subsidiary", ledger.getSubsidiary());
                    return ledgerMap;
                }).collect(Collectors.toList());
                response.setEntity(ledgersResponse);
            } else {
                response.setMessage("No entries found");
                response.setStatusCode(HttpStatus.NO_CONTENT.value());
                response.setEntity(null);
            }
        } catch (Exception e) {

            response.setMessage("Error occurred while fetching ledgers");
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setEntity(null);
        }
        return response;
    }
//    public Ledger generateBalanceSheet() {
//        List<Ledger> ledgerEntries = ledgerRepo.findAll();
//        double totalAssets = 0;
//        double totalLiabilities = 0;
//        double equity = 0;
//
//        for (Ledger entry : ledgerEntries) {
//            if (entry.getAccountType().equals("Balance Sheet")) {
//                if (entry.getNet() > 0) {
//                    totalAssets += entry.getNet();
//                } else {
//                    totalLiabilities -= entry.getNet(); // Subtracting the negative amount to make it positive
//                }
//            } else if (entry.getAccountType().equals("Equity")) {
//                equity += entry.getNet();
//            }
//
//    }
//}
}

