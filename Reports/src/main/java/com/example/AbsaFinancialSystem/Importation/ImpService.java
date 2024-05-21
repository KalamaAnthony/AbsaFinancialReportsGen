
package com.example.AbsaFinancialSystem.Importation;

import com.example.AbsaFinancialSystem.SubcatConfig.SubCategory;
import com.example.AbsaFinancialSystem.SubcatConfig.SubclassRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.AbsaFinancialSystem.Importation.Imp.convertNetValue;

@Service
@Slf4j
public class ImpService {

    @Autowired
    private ImpRepo impRepo;
    @Autowired
    SubclassRepository subclassRepository;

    public List<Imp> loadExcelFile(MultipartFile file) throws IOException {
        List<Imp> records = new ArrayList<>();
        try (InputStream fileInputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(fileInputStream)) {
            Sheet sheet = workbook.getSheetAt(0);



            for (var curRow : sheet) {
                if (curRow == null || curRow.getRowNum() == 0) {
                    continue; // Skip header row
                }

                boolean isEmptyRow = true;
                for (var cell : curRow) {
                    if (cell != null && cell.getCellType() != CellType.BLANK) {
                        isEmptyRow = false;
                        break;
                    }
                }

                if (isEmptyRow) {
                    continue;
                }


                var record = new Imp();
                record.setPeriod(getCellStringValue(curRow.getCell(0)));
                record.setAccount(parseLongFromCell(curRow.getCell(1)));
                record.setAccountDescription(getCellStringValue(curRow.getCell(2)));
//                record.setPlOrBs(getCellStringValue(curRow.getCell(3)));
                log.info(" step 5");
                Optional<SubCategory> subCategory= subclassRepository.findBySubcategory(getCellStringValue(curRow.getCell(4)));

                record.setSubcategory(subCategory.get());

                var clientValue = getCellStringValue(curRow.getCell(5));
                if (!clientValue.isEmpty()) {
                    BigDecimal netValue = convertNetValue(clientValue);
                    record.setNet(netValue);
                } else {
                    record.setNet(null);
                }

                records.add(record);
            }
        } catch (IOException e) {
            log.error("Error processing Excel file", e);
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
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> BigDecimal.valueOf(cell.getNumericCellValue()).toPlainString();
            default -> "";
        };
    }

    private Long parseLongFromCell(Cell cell) {
        if (cell == null) {
            return null;
        }
        return switch (cell.getCellType()) {
            case NUMERIC -> (long) cell.getNumericCellValue();
            case STRING -> {
                var value = cell.getStringCellValue().trim();
                if (!value.isEmpty()) {
                    try {
                        yield Long.valueOf(value.split("\\.")[0]);
                    } catch (NumberFormatException e) {
                        log.error("Invalid number format for value: {}", value, e);
                    }
                }
                yield null;
            }
            default -> null;
        };
    }
}
