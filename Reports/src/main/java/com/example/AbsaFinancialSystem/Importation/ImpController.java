package com.example.AbsaFinancialSystem.Importation;

import com.example.AbsaFinancialSystem.SubcatConfig.SubCategory;
import com.example.AbsaFinancialSystem.SubcatConfig.SubclassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController





public class ImpController {

    @Autowired
    ImpService impService= new ImpService();
    @Autowired
    SubclassRepository repo;

    @Autowired
    ImpRepo impRepo;


    @PostMapping(value = "/import",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<Imp>> uploadFile(@RequestPart("file") MultipartFile file) {
        try {
            List<Imp> records = impService.loadExcelFile(file);
            return ResponseEntity.ok(records);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);
        }

    }

    @GetMapping("/getBalanceSheet")
    public ResponseEntity<?> getBalanceSheet(@RequestParam("subId") int subId){
        try{
            List<String> accountCategories = repo.findAllAccountCategories(subId);
            System.out.println("Step 2");
            List<BalanceSheetAccCategory> balanceSheetAccountCategories = new ArrayList<>();
            System.out.println("Step 3");
            String balanceSheet = "";


            for(String accType: accountCategories){
                System.out.println("current account type ---->" + accType);

                if(accType.toLowerCase().contains("bs")){

                    System.out.println("Step 4");
                    balanceSheet = accType;
                }
            }
            //getting all accounts to be used in the balance sheet.
            System.out.println(balanceSheet);
            List<String> subsidiaryCategories = repo.findAllSubsidiaryCategories(subId, balanceSheet);


            for(String accCategory: subsidiaryCategories){
                long sum = 0;
                System.out.println("Step 5");

                String currentCategory = accCategory;
                System.out.println("oyaaaaaaaaaaaaaaaaaaaaaaa---- Category babaaaa "+ currentCategory);
                BalanceSheetCat sheetCategory = new BalanceSheetCat();
                sheetCategory.setTitle(currentCategory);

                //getting all sub categories under the top category
                List<String> currentCategorySubCategories = repo.findAllAccountSubCategories(subId, currentCategory);

               List<Long> categoryIds= new ArrayList<Long>();
                for (String accSubCat: currentCategorySubCategories){
                    System.out.println("oyaaaaaaaaaaaaaaaaaaaaaaa tuko kwa sub category----"+ accSubCat);
                    //getting the id of each subcategory
                    long categoryTypeId = repo.findAccountCategoryId(accSubCat);
                   categoryIds.add(categoryTypeId);


                }
//                List<Object[]> result = impRepo.findSumBySubcategoryIdAndName(categoryIds);
//                System.out.println(result);
                long totalSum = 0;
                for (Long categoryId : categoryIds) {
                    long total= impRepo.findSumOfSubcategories(categoryId);
                    totalSum +=total;
                }
                System.out.println("oyaaaaaaaaaaaaaaaaaaaaaaa tuko kwa sub category----"+ totalSum);

                //get all accounts in the given sub category
                // sum net value of each sub category

                // selecting all accounts to be used in balance sheet

            }

           // List<SubCategory>
            System.out.println(subsidiaryCategories);
            return ResponseEntity.status(200).body(balanceSheetAccountCategories);

        }catch(Exception e){
            return ResponseEntity.status(500).body(e.getMessage());
        }

    }
}