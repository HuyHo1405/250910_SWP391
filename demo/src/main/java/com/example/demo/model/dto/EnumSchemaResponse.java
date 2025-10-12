package com.example.demo.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class EnumSchemaResponse {
    private final String type = "string";
    private String name;
    private List<String> enumValue;
    private String description;

    public EnumSchemaResponse(String name, List<String> enumValue, String description) {
        this.name = name;
        this.enumValue = enumValue;
        this.description = description;
    }
}
