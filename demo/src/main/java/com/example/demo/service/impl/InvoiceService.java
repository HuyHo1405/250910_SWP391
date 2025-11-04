package com.example.demo.service.impl;

import com.example.demo.exception.CommonException; // ✅ Import
import com.example.demo.model.dto.InvoiceLineResponse;
import com.example.demo.model.dto.InvoiceRequest;
import com.example.demo.model.dto.InvoiceResponse;
import com.example.demo.model.entity.*;
import com.example.demo.model.modelEnum.InvoiceItemType;
import com.example.demo.model.modelEnum.InvoiceStatus;
import com.example.demo.repo.BookingRepo;
import com.example.demo.repo.InvoiceRepo;
import com.example.demo.repo.MaintenanceCatalogModelPartRepo;
import com.example.demo.repo.MaintenanceCatalogRepo;
import com.example.demo.service.interfaces.IInvoiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InvoiceService implements IInvoiceService {

    private final BookingRepo bookingRepo;
    private final InvoiceRepo invoiceRepo;
    private final MaintenanceCatalogRepo catalogRepo;
    private final MaintenanceCatalogModelPartRepo catalogModelPartRepo;

    // 1. Tạo invoice cho một booking, gồm dịch vụ & part
    @Override
    public InvoiceResponse create(Long bookingId) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new CommonException.NotFound("Booking", bookingId)); // ✅ Sửa

        if (invoiceRepo.findByBookingId(bookingId).isPresent())
            throw new CommonException.AlreadyExists("Invoice", "BookingId", bookingId); // ✅ Sửa

        Invoice invoice = new Invoice();
        invoice.setBooking(booking);
        invoice.setIssueDate(LocalDateTime.now());
        invoice.setStatus(InvoiceStatus.DRAFT);

        List<InvoiceLine> lines = new ArrayList<>();

        // Duyệt toàn bộ bookingDetail (dịch vụ đặt)
        for (BookingDetail detail : booking.getBookingDetails()) {
            MaintenanceCatalogModel catalogModel = detail.getCatalogModel();
            Long catalogId = catalogModel.getMaintenanceCatalog().getId();
            Long modelId = catalogModel.getVehicleModel().getId();

            lines.addAll(buildInvoiceLine(catalogId, modelId, invoice));
        }
        invoice.setLines(lines);
        BigDecimal totalAmount = lines.stream()
                .map(line -> line.getQuantity().multiply(line.getUnitPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        invoice.setTotalAmount(totalAmount);


        invoiceRepo.save(invoice);

        return mapToResponse(invoice);
    }

    // 2. Cập nhật invoice (trả về DTO đã chỉnh)
    @Override
    public InvoiceResponse update(Long bookingId, InvoiceRequest.Update update) {
        Invoice invoice = invoiceRepo.findByBookingId(bookingId)
                .orElseThrow(() -> new CommonException.NotFound("Invoice cho BookingId", bookingId)); // ✅ Sửa

        // Tùy luồng business, chỉnh sửa các field update hợp lệ
        if(update != null && update.getStatus() != null){
            invoice.setStatus(update.getStatus());
        }

        if(update != null && update.getDueDate() != null){
            invoice.setDueDate(update.getDueDate());
        }

        invoiceRepo.save(invoice);

        return mapToResponse(invoice);
    }

    @Override
    @Transactional
    public InvoiceResponse recalculateAndFinalize(Long bookingId) {
        log.info("Recalculating and finalizing invoice for booking {}", bookingId);

        // 1. Lấy Booking (Nguồn sự thật) và Invoice (Thứ cần cập nhật)
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new CommonException.NotFound("Booking", bookingId)); // ✅ Sửa
        Invoice invoice = invoiceRepo.findByBookingId(bookingId)
                .orElseThrow(() -> new CommonException.NotFound("Invoice cho BookingId", bookingId)); // ✅ Sửa

        // 2. Chỉ cho phép chốt sổ khi đang ở DRAFT
        if (invoice.getStatus() != InvoiceStatus.DRAFT) {
            throw new CommonException.Conflict("INVALID_INVOICE_STATE", // ✅ Sửa
                    "Chỉ có thể chốt hóa đơn từ trạng thái DRAFT. Trạng thái hiện tại: " + invoice.getStatus());
        }

        // 3. XÓA SẠCH các lines cũ
        invoice.getLines().clear();
        invoiceRepo.flush();

        // 4. TẠO LẠI TOÀN BỘ lines từ Booking (NGUỒN SỰ THẬT)
        List<InvoiceLine> newLines = new ArrayList<>();
        for (BookingDetail detail : booking.getBookingDetails()) {
            MaintenanceCatalogModel catalogModel = detail.getCatalogModel();
            Long catalogId = catalogModel.getMaintenanceCatalog().getId();
            Long modelId = catalogModel.getVehicleModel().getId();

            newLines.addAll(buildInvoiceLine(catalogId, modelId, invoice));
        }
        invoice.setLines(newLines);

        // 5. TÍNH TOÁN LẠI TỔNG TIỀN
        BigDecimal totalAmount = newLines.stream()
                .map(line -> line.getQuantity().multiply(line.getUnitPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP); // Làm tròn về 2 chữ số thập phân
        invoice.setTotalAmount(totalAmount);

        // 6. CHỐT TRẠNG THÁI -> SẴN SÀNG ĐỂ THANH TOÁN
        invoice.setStatus(InvoiceStatus.UNPAID);

        Invoice savedInvoice = invoiceRepo.save(invoice);
        log.info("Invoice {} for booking {} finalized. Total: {}. Status: UNPAID.", savedInvoice.getId(), bookingId, totalAmount);

        return mapToResponse(savedInvoice);
    }


    // 3. Tìm invoice theo id (trả về DTO)
    @Override
    public InvoiceResponse findByBookingId(Long bookingId) {
        Invoice invoice = invoiceRepo.findByBookingId(bookingId)
                .orElseThrow(() -> new CommonException.NotFound("Invoice cho BookingId", bookingId)); // ✅ Sửa
        return mapToResponse(invoice);
    }

    // 4. Xóa invoice theo id
    @Override
    public void deleteById(Long invoiceId) {
        Invoice invoice = invoiceRepo.findById(invoiceId)
                .orElseThrow(() -> new CommonException.NotFound("Invoice", invoiceId)); // ✅ Sửa
        invoiceRepo.delete(invoice);
    }

    // 5. Build InvoiceLine cho 1 dịch vụ & model (Catalog + Model)
    public List<InvoiceLine> buildInvoiceLine(Long catalogId, Long modelId, Invoice invoice){
        MaintenanceCatalog catalog = catalogRepo.findById(catalogId)
                .orElseThrow(() -> new CommonException.NotFound("MaintenanceCatalog", catalogId)); // ✅ Sửa

        MaintenanceCatalogModel selectedModel = null;
        for (MaintenanceCatalogModel model : catalog.getModels()) {
            if ((modelId).equals(model.getVehicleModel().getId())) {
                selectedModel = model;
                break;
            }
        }
        if (selectedModel == null)
            throw new CommonException.NotFound( // ✅ Sửa
                    String.format("Không tìm thấy model %d phù hợp trong catalog %d", modelId, catalogId)
            );

        List<InvoiceLine> lines = new ArrayList<>();
        // 1. Dịch vụ chính
        InvoiceLine serviceLine = new InvoiceLine();
        serviceLine.setInvoice(invoice);
        serviceLine.setItemDescription(catalog.getName());
        serviceLine.setItemType(InvoiceItemType.SERVICE);
        serviceLine.setQuantity(BigDecimal.ONE);
        serviceLine.setUnitPrice(selectedModel.getMaintenancePrice());
        lines.add(serviceLine);

        // 2. Các phụ tùng thuộc model này
        for (MaintenanceCatalogModelPart partRel : catalogModelPartRepo.findByMaintenanceCatalogIdAndVehicleModelId(catalogId, modelId)) {
            InvoiceLine partLine = new InvoiceLine();
            partLine.setInvoice(invoice);
            partLine.setItemDescription(partRel.getPart().getName());
            partLine.setItemType(InvoiceItemType.PART);
            partLine.setQuantity(partRel.getQuantityRequired());
            partLine.setUnitPrice(partRel.getPart().getCurrentUnitPrice());
            lines.add(partLine);
        }

        return lines;
    }

    // ... (toLineResponses và mapToResponse không đổi) ...
    private List<InvoiceLineResponse> toLineResponses(List<InvoiceLine> lines) {
        return lines.stream().map(line -> InvoiceLineResponse.builder()
                        .id(line.getId())
                        .itemDescription(line.getItemDescription())
                        .itemType(line.getItemType())
                        .quantity(line.getQuantity())
                        .unitPrice(line.getUnitPrice())
                        .build())
                .collect(Collectors.toList());
    }

    private InvoiceResponse mapToResponse(Invoice invoice) {
        return InvoiceResponse.builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .issueDate(invoice.getIssueDate())
                .dueDate(invoice.getDueDate())
                .totalAmount(invoice.getTotalAmount())
                .status(invoice.getStatus())
                .createdAt(invoice.getCreatedAt())
                .invoiceLines(toLineResponses(invoice.getLines()))
                .build();
    }

}