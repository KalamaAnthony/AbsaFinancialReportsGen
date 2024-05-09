package com.example.AbsaFinancialSystem.SubsidiaryComponent;

import com.example.AbsaFinancialSystem.Utilities.EntityResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("api/v1/subsidiary")
public class SubsidiaryController {

    @Autowired

    SubsidiaryService subsidiaryService;

    @PostMapping("/create")
public EntityResponse add (@RequestBody Subsidiary subsidiary){
    return subsidiaryService.add(subsidiary);
}
}
