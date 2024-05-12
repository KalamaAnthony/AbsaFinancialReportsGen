package com.example.AbsaFinancialSystem.SubsidiaryComponent;

import com.example.AbsaFinancialSystem.Utilities.EntityResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@CrossOrigin
@RequestMapping("api/v1/subsidiary")
public class SubsidiaryController {

    @Autowired

    SubsidiaryService subsidiaryService;

    @PostMapping("/create")
    public EntityResponse add (@RequestBody Subsidiary subsidiary){
        return subsidiaryService.add(subsidiary);
    }

    @PutMapping("update")
    public EntityResponse<Subsidiary> update(@RequestBody Subsidiary updatedSubsidiary){
        return subsidiaryService.updated(updatedSubsidiary);
    }

}