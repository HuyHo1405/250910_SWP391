package com.example.demo.model.dto;

import com.example.demo.model.modelEnum.PartCategory;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartResponse {
    private Long id;
    private String name;
    private String partNumber;
    private String manufacturer;
    private String category;
    private BigDecimal currentUnitPrice;
    private BigDecimal quantity;        // Số lượng còn trong kho
    private BigDecimal reserved;        // Số lượng đã đặt trước
    private BigDecimal used;            // Số lượng đã sử dụng
    private BigDecimal all;             // Tổng số lượng = quantity + used
    private String imageUrl;            // URL hình ảnh của phụ tùng
    private String status;              // Enum EntityStatus (ACTIVE, INACTIVE, etc.)
    private LocalDateTime createdAt;

    // Enum schema cho TẤT CẢ catalogs sử dụng part này
    private EnumSchemaResponse catalogsEnum;

    // Enum schema cho TẤT CẢ vehicle models sử dụng part này
    private EnumSchemaResponse vehicleModelsEnum;

    // Mapping: catalog name -> list of vehicle models
    // VD: { "Thay lọc gió cabin": ["VinFast VF 3", "VinFast VF 5"] }
    private Map<String, List<String>> catalogVehicleMapping;
}
