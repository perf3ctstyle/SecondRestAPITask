package com.epam.esm.dto;

import java.util.Map;

public class SearchInfoDto {

    private Map<String, String> fieldAndValueForPartialSearch;
    private String fieldForSorting;
    private Boolean isAscending;

    public SearchInfoDto() {
    }

    public void setFieldAndValueForPartialSearch(Map<String, String> fieldAndValueForPartialSearch) {
        this.fieldAndValueForPartialSearch = fieldAndValueForPartialSearch;
    }

    public Map<String, String> getFieldAndValueForPartialSearch() {
        return fieldAndValueForPartialSearch;
    }

    public void setFieldForSorting(String fieldForSorting) {
        this.fieldForSorting = fieldForSorting;
    }

    public String getFieldForSorting() {
        return fieldForSorting;
    }

    public void setIsAscending(Boolean isAscending) {
        this.isAscending = isAscending;
    }

    public Boolean getIsAscending() {
        return isAscending;
    }
}
