package com.epam.esm.dto;

public class TagCostDto {

    private Long id;
    private String name;
    private Integer costSum;

    public TagCostDto() {

    }

    public TagCostDto(Long id, String name, Integer costSum) {
        this.id = id;
        this.name = name;
        this.costSum = costSum;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCostSum() {
        return costSum;
    }

    public void setCostSum(Integer costSum) {
        this.costSum = costSum;
    }
}
