package com.example.AbsaFinancialSystem.SubcatConfig;



import com.example.AbsaFinancialSystem.Utilities.EntityResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@AllArgsConstructor
public class SubcatService {
    private SubclassRepository subclassRepository;
    public EntityResponse createSubcat(String subcategory, int subsidiaryId, Enumsubclasses enumsubclasses, SubCatEnum subCatEnum) {
        EntityResponse entityResponse = new EntityResponse<>();
        try {
            Optional<SubCategory> existingSubcat = subclassRepository.findBySubcategory(subcategory);
            if (existingSubcat.isEmpty()) {
                SubCategory subClass = createSubClass(subcategory, subsidiaryId, enumsubclasses, subCatEnum);
                subclassRepository.save(subClass);

                entityResponse.setMessage("Subclass created successfully");
                entityResponse.setStatusCode(HttpStatus.OK.value());
                entityResponse.setEntity(subClass);
            } else {
                entityResponse.setMessage("This Subcategory Already Exists");
                entityResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            entityResponse.setMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase() + exception.getLocalizedMessage());
            entityResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            entityResponse.setEntity(null);
        }
        return entityResponse;
    }

    private SubCategory createSubClass(String subcategory, int subsidiaryId, Enumsubclasses enumsubclasses, SubCatEnum subCatEnum) {
        SubCategory subClass = new SubCategory();
        subClass.setSubcategory(subcategory);
        subClass.setSubsidiaryId(subsidiaryId);

        if (enumsubclasses != null) {
            subClass.setEnumsubclasses(enumsubclasses);
            subClass.setAccountType(enumsubclasses.getSubCatEnum());
            subClass.setCategory(enumsubclasses.getSubCatEnum().getCatEnum());
        } else {
            // If enumsubclasses is not provided, set default values
            subClass.setEnumsubclasses(Enumsubclasses.NOT_VALID);
            subClass.setAccountType(SubCatEnum.NOT_VALID);
            subClass.setCategory(subCatEnum.getCatEnum());
        }

        return subClass;
    }






}

