package com.example.AbsaFinancialSystem.SubcatConfig;

public enum Enumsubclasses {
    FIXED_ASSETS(SubCatEnum.ASSET, "FIXED_ASSETS"),
    CURRENT_ASSETS(SubCatEnum.ASSET, "CURRENT_ASSETS"),

    NON_CURRENT_ASSET(SubCatEnum.ASSET, "NON_CURRENT_ASSETS"),
    SHORT_TERM(SubCatEnum.LIABILITY, "SHORT_TERM"),
    LONG_TERM(SubCatEnum.LIABILITY, "LONG_TERM"),
    EQUITY (SubCatEnum.EQUITY,"EQUITY"),
    NOT_VALID(SubCatEnum.NOT_VALID,"NOT_VALID");

    private final SubCatEnum subCatEnum;
    private final String description;

    Enumsubclasses(SubCatEnum subCatEnum, String description) {
        this.subCatEnum = subCatEnum;
        this.description = description;
    }

    public SubCatEnum getSubCatEnum() {
        return subCatEnum;
    }

    public String getDescription() {
        return description;
    }
}




