package com.example.AbsaFinancialSystem.SubsidiaryComponent;

import com.example.AbsaFinancialSystem.Utilities.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class SubsidiaryService {
    @Autowired

     SubsidiaryRepo subsidiaryRepo;
    public EntityResponse add(Subsidiary subsidiary) {
        EntityResponse response = new EntityResponse<>();
        try{
            Optional<Subsidiary> checkSubsidiary = subsidiaryRepo.findById(subsidiary.getId());
            if(checkSubsidiary.isPresent()){
                response.setMessage("Subsidiary already exist");
            }else{
                Subsidiary savedSubsidiary = subsidiaryRepo.save(subsidiary);
                response.setMessage("Subsidiary created successfully");
                response.setEntity(subsidiary);
                response.setStatusCode(HttpStatus.CREATED.value());

                subsidiaryRepo.save(savedSubsidiary);
            }
        }catch(Exception e){
            log.error("An error occured");
        }
        return response;
    }
}
