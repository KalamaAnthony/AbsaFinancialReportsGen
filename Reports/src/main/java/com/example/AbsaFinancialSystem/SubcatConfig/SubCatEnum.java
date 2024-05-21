package com.example.AbsaFinancialSystem.SubcatConfig;

import lombok.Getter;

public enum SubCatEnum {
    ASSET(CategoryEnum.BS,"ASSET"),
    LIABILITY( CategoryEnum.BS,"LIABILITY"),

    EQUITY(CategoryEnum.BS,"EQUITY"),

    REVENUE(CategoryEnum.PL,"REVENUE"),

    EXPENSE(CategoryEnum.PL,"EXPENSE"),

    NOT_VALID(CategoryEnum.NOT_VALID,"NOT_VALID");

    private CategoryEnum categoryEnum;

    @Getter
    private String  description;

    SubCatEnum (CategoryEnum categoryEnum, String description) {
        this.categoryEnum = categoryEnum;
        this.description = description;
    }

    public CategoryEnum getCatEnum() {
        return categoryEnum;
    }


}

