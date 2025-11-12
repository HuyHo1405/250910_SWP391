package com.example.demo.model.dto;

import com.example.demo.model.modelEnum.PartCategory;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

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

    @NotNull(message = "Danh mục không được để trống")
    private String category;

    @NotNull(message = "Đơn giá không được để trống")
    @DecimalMin(value = "0.0", message = "Đơn giá phải lớn hơn hoặc bằng 0")
    private BigDecimal currentUnitPrice;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 0, message = "Số lượng phải lớn hơn hoặc bằng 0")
    private BigDecimal quantity;

    @NotNull(message = "Số lượng hàng đã đặt không được trống")
    @Min(value = 0, message = "Số lượng phải lớn hơn hoặc bằng 0")
    private BigDecimal reserved;

    @NotNull(message = "Số lượng đã sử dụng không được trống")
    @Min(value = 0, message = "Số lượng phải lớn hơn hoặc bằng 0")
    private BigDecimal used;

    @Size(max = 500, message = "URL hình ảnh không được vượt quá 500 ký tự")
    private String imageUrl;
}
