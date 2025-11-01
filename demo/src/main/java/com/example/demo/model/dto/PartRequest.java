package com.example.demo.model.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartRequest {

    @NotBlank(message = "Tên phụ tùng không được để trống")
    @Size(max = 255, message = "Tên phụ tùng không được vượt quá 255 ký tự")
    private String name;

    @NotNull(message = "Mã phụ tùng không được để trống")
    private String partNumber;

    @NotBlank(message = "Nhà sản xuất không được để trống")
    @Size(max = 255, message = "Tên nhà sản xuất không được vượt quá 255 ký tự")
    private String manufacturer;

    @Size(max = 1000, message = "Mô tả không được vượt quá 1000 ký tự")
    private String description;

    @NotNull(message = "Đơn giá không được để trống")
    @DecimalMin(value = "0.0", inclusive = true, message = "Đơn giá phải lớn hơn hoặc bằng 0")
    private Double currentUnitPrice;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 0, message = "Số lượng phải lớn hơn hoặc bằng 0")
    private Integer quantity;

    @NotNull(message = "Số lượng hàng đã đặt không được trống")
    @Min(value = 0, message = "Số lượng phải lớn hơn hoặc bằng 0")
    private Integer reserved;
}
