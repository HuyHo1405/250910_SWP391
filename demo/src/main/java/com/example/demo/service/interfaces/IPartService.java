package com.example.demo.service.interfaces;

import com.example.demo.model.dto.PartRequest;
import com.example.demo.model.dto.PartResponse;
import com.example.demo.model.modelEnum.EntityStatus;

import java.util.List;

public interface IPartService {

    // ================================
    // CREATE - Tạo mới part
    // ================================
    PartResponse createPart(PartRequest request);

    // ================================
    // READ - Lấy thông tin part
    // ================================
    PartResponse getPartById(Long id);

    PartResponse getPartByPartNumber(String partNumber);

    List<PartResponse> getAllPartsFiltered(String manufacturer, EntityStatus status, String searchKeyword);

    List<PartResponse> getLowStockParts(Integer threshold); // Parts có quantity <= threshold

    // ================================
    // UPDATE - Cập nhật part
    // ================================
    PartResponse updatePart(Long id, PartRequest request);

    PartResponse updatePartPrice(Long id, Double newPrice);

    PartResponse adjustPartStock(Long id, Integer adjustment); // Tăng/giảm quantity (+/- adjustment)

    PartResponse adjustReservedStock(Long id, Integer adjustment); // Tăng/giảm quantity (+/- adjustment)

    PartResponse updatePartStatus(Long id, EntityStatus status);

    // ================================
    // DELETE - Xóa part
    // ================================
    void deletePart(Long id); // Hard delete

    PartResponse deactivatePart(Long id); // Soft delete (set status = INACTIVE)

    PartResponse reactivatePart(Long id); // Reactivate (set status = ACTIVE)

    // ================================
    // INVENTORY - Quản lý tồn kho
    // ================================
    PartResponse increaseStock(Long id, Integer amount); // Nhập kho

    PartResponse decreaseStock(Long id, Integer amount); // Xuất kho

    boolean checkAvailability(Long id, Integer requiredQuantity); // Kiểm tra đủ hàng không
}
