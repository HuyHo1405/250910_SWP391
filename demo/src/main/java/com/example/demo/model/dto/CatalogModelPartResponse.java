package com.example.demo.model.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CatalogModelPartResponse {
    private Long partId;                  // ID part
    private String partName;              // Tên linh kiện
    private BigDecimal quantityRequired;     // Số lượng cần dùng
    private Boolean isOptional;           // Tuỳ chọn, không bắt buộc
    private String notes;                 // Ghi chú nếu có
}
