package com.example.demo.model.modelEnum;

import lombok.Getter;

@Getter
public enum     DateTimeFormat {
    iso("ISO"),
    timestamp("timestamp"),
    custom("yyyy-MM-dd HH:mm:ss");

    private final String value;

    DateTimeFormat(String value) {
        this.value = value;
    }

}
