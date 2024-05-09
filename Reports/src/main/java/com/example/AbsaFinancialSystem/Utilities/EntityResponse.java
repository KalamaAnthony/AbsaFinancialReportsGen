package com.example.AbsaFinancialSystem.Utilities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntityResponse <T>{
    private String message;
    private Integer statusCode;
     private T Entity;
}
