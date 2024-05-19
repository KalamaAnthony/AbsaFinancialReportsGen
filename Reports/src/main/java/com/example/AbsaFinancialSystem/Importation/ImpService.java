package com.example.AbsaFinancialSystem.Importation;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.example.AbsaFinancialSystem.Importation.Imp.convertNetValue;

@Service
@Slf4j
public class ImpService {

    @Autowired
    private ImpRepo impRepo;

    public List<Imp> loadExcelFile(MultipartFile file) throws IOException {
        List<Imp> records = new ArrayList<>();
        try (InputStream fileInputStream = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(fileInputStream);
            Sheet sheet = workbook.getSheetAt(0);

            for (Row curRow : sheet) {
                if (curRow == null || curRow.getRowNum() == 0) {
                    continue; // Skip header row
                }

                boolean isEmptyRow = true;
                for (Cell cell : curRow) {
                    if (cell != null && cell.getCellType() != CellType.BLANK) {
                        isEmptyRow = false;
                        break;
                    }
                }

                if (isEmptyRow) {
                    continue;
                }

                Imp record = new Imp();
                record.setPeriod(getCellStringValue(curRow.getCell(0)));
                record.setAccount(parseLongFromCell(curRow.getCell(1)));
                record.setAccountDescription(getCellStringValue(curRow.getCell(2)));
                record.setPlOrBs(getCellStringValue(curRow.getCell(3)));
                record.setSubCategory(getCellStringValue(curRow.getCell(4)));
                String clientValue = getCellStringValue(curRow.getCell(5));
                if (!clientValue.isEmpty()) {
                    BigDecimal netValue = convertNetValue(clientValue);
                    record.setNet(netValue);
                } else {
                    record.setNet(null);
                }

                records.add(record);
            }

            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }

        // Save records to the database
        impRepo.saveAll(records);
        return records;
    }

    private String getCellStringValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                return BigDecimal.valueOf(cell.getNumericCellValue()).toPlainString();
            default:
                return "";
        }
    }

    private Long parseLongFromCell(Cell cell) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case NUMERIC:
                return (long) cell.getNumericCellValue();
            case STRING:
                String value = cell.getStringCellValue().trim();
                if (!value.isEmpty()) {
                    try {
                        return Long.valueOf(value.split("\\.")[0]);
                    } catch (NumberFormatException e) {
                        log.error("Invalid number format for value: {}", value, e);
                    }
                }
                return null;
            default:
                return null;
        }
    }
}
