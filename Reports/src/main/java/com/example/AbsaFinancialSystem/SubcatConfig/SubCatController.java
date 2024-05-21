package com.example.AbsaFinancialSystem.SubcatConfig;



import com.example.AbsaFinancialSystem.Utilities.EntityResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/SubcatController")
@AllArgsConstructor
public class SubCatController {

    private SubcatService service;

    @PostMapping("/createSubclasses")
    public EntityResponse addSubcategory(@RequestParam String Subcategory,
                                         @RequestParam int subsidiaryId,
                                         @RequestParam(required = false) Enumsubclasses enumsubclasses,
                                         @RequestParam (required = false) SubCatEnum subCatEnum){
        return service.createSubcat(Subcategory,subsidiaryId,enumsubclasses,subCatEnum);
    }
}

