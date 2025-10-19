package com.example.demo.model.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceCatalogModelPartResponse {
    private Long partId;                  // ID part
    private String partName;              // Tên linh kiện
    private Integer quantityRequired;     // Số lượng cần dùng
    private Boolean isOptional;           // Tuỳ chọn, không bắt buộc
    private String notes;                 // Ghi chú nếu có
}
