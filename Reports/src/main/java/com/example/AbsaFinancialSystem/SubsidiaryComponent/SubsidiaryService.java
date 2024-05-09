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

    public EntityResponse<Subsidiary> updated(Subsidiary updatedSubsidiary) {
        EntityResponse<Subsidiary> entityResponse=new EntityResponse<>();
        Optional<Subsidiary> existingSubsidiaryOptional=subsidiaryRepo.findById(updatedSubsidiary.getId());
        try{
            if(existingSubsidiaryOptional.isPresent()){
                Subsidiary existingSubsidiary=existingSubsidiaryOptional.get();
                existingSubsidiary.setSubsidiaryName(updatedSubsidiary.getSubsidiaryName());
                existingSubsidiary.setCreatedOn(updatedSubsidiary.getCreatedOn());

                Subsidiary savedSubsidiary=subsidiaryRepo.save(updatedSubsidiary);
                entityResponse.setMessage("subsidiary updated successfully");
                entityResponse.setStatusCode(HttpStatus.OK.value());
                entityResponse.setEntity(savedSubsidiary);
            }else{
                entityResponse.setMessage("subsidiary not found");
                entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                entityResponse.setEntity(null);
            }
        }catch(Exception e){
            log.error("An error occurred while updating subsidiary",e);
            entityResponse.setMessage("An error occurred while updating subsidiary"+e.getMessage());
            entityResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            entityResponse.setEntity(null);
        }
        return entityResponse;
    }
}