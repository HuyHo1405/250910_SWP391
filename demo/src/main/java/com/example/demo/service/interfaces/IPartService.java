package com.example.demo.service.interfaces;

import com.example.demo.model.dto.PartRequest;
import com.example.demo.model.dto.PartResponse;
import com.example.demo.model.modelEnum.EntityStatus;

import java.math.BigDecimal;
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

    List<PartResponse> getLowStockParts(BigDecimal threshold); // Parts có quantity <= threshold

    // ================================
    // UPDATE - Cập nhật part
    // ================================
    PartResponse updatePart(Long id, PartRequest request);

    PartResponse updatePartPrice(Long id, BigDecimal newPrice);

    PartResponse updatePartStatus(Long id, EntityStatus status);

    // ================================
    // STOCK ADJUSTMENT - Quản lý tồn kho
    // ================================
    PartResponse adjustPartStock(Long id, BigDecimal adjustment); // Tăng/giảm quantity (+/- adjustment)

    PartResponse adjustReservedStock(Long id, BigDecimal adjustment); // Tăng/giảm quantity (+/- adjustment)

    boolean checkAvailability(Long id, BigDecimal requiredQuantity); // Kiểm tra đủ hàng không

    // ================================
    // DELETE - Xóa part
    // ================================
    void deletePart(Long id); // Hard delete

    PartResponse deactivatePart(Long id); // Soft delete (set status = INACTIVE)

    PartResponse reactivatePart(Long id); // Reactivate (set status = ACTIVE)
}
