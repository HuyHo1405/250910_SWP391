package com.example.demo.controller;

import com.example.demo.model.dto.EnumSchemaResponse;
import org.springframework.http.ResponseEntity;

public interface EnumSchemaController {
    ResponseEntity<EnumSchemaResponse> getEnumSchema(String name);
}
