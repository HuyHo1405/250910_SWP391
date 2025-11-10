package com.example.demo.service.impl;

import com.example.demo.exception.CommonException; // ✅ Import
import com.example.demo.model.dto.InvoiceLineResponse;
import com.example.demo.model.dto.InvoiceRequest;
import com.example.demo.model.dto.InvoiceResponse;
import com.example.demo.model.entity.*;
import com.example.demo.model.modelEnum.InvoiceItemType;
import com.example.demo.model.modelEnum.InvoiceStatus;
import com.example.demo.repo.*;
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

    private final AccessControlService accessControlService;

    private final BookingRepo bookingRepo;
    private final InvoiceRepo invoiceRepo;
    private final MaintenanceCatalogModelRepo catalogModelRepo;
    private final MaintenanceCatalogModelPartRepo catalogModelPartRepo;

    // 1. Tạo invoice cho một booking, gồm dịch vụ & part
    @Override
    public InvoiceResponse create(Long bookingId) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new CommonException.NotFound("Booking", bookingId)); // ✅ Sửa

        accessControlService.verifyCanAccessAllResources("INVOICE", "create");

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

            lines.addAll(buildInvoiceLine(catalogModel.getId(), invoice));
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
    public InvoiceResponse updateById(Long bookingId, InvoiceRequest.UpdateInvoice update) {
        Invoice invoice = invoiceRepo.findById(bookingId)
                .orElseThrow(() -> new CommonException.NotFound("Invoice cho BookingId", bookingId)); // ✅ Sửa

        accessControlService.verifyCanAccessAllResources("INVOICE", "update");

        if(invoice.getStatus() ==  InvoiceStatus.PAID)
            throw new CommonException.InvalidOperation("Không thể chỉnh sửa hóa đơn đã thanh toán");

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

    // 3. Tìm invoice theo id (trả về DTO)
    @Override
    public InvoiceResponse findById(Long id) {
        Invoice invoice = invoiceRepo.findById(id)
                .orElseThrow(() -> new CommonException.NotFound("Invoice id", id)); // ✅ Sửa
        return mapToResponse(invoice);
    }

    // 4. Xóa invoice theo id
    @Override
    public void deleteById(Long invoiceId) {
        Invoice invoice = invoiceRepo.findById(invoiceId)
                .orElseThrow(() -> new CommonException.NotFound("Invoice", invoiceId)); // ✅ Sửa

        if(invoice.getStatus() == InvoiceStatus.PAID){
            throw new CommonException.InvalidOperation("Không thể xóa hóa đơn đã thanh toán");
        }

        invoiceRepo.delete(invoice);
    }

    // 5. Build InvoiceLine cho 1 dịch vụ & model (Catalog + Model)
    public List<InvoiceLine> buildInvoiceLine(Long catalogModelId, Invoice invoice){
        MaintenanceCatalogModel selectedModel = catalogModelRepo.findById(catalogModelId)
                .orElseThrow(() -> new CommonException.NotFound("MaintenanceCatalogModel", catalogModelId));

        MaintenanceCatalog catalog = selectedModel.getMaintenanceCatalog();

        List<InvoiceLine> lines = new ArrayList<>();
        // 1. Dịch vụ chính
        InvoiceLine serviceLine = new InvoiceLine();
        serviceLine.setInvoice(invoice);
        serviceLine.setItemDescription(catalog.getName());
        serviceLine.setItemType(InvoiceItemType.SERVICE);
        serviceLine.setQuantity(BigDecimal.ONE);
        serviceLine.setUnitPrice(selectedModel.getMaintenancePrice());
        serviceLine.calculateAmounts(); // ← THÊM: Tính lineTotal ngay
        lines.add(serviceLine);

        // 2. Các phụ tùng thuộc model này
        for (MaintenanceCatalogModelPart partRel : catalogModelPartRepo.findByMaintenanceCatalogModelId(catalogModelId)) {
            InvoiceLine partLine = new InvoiceLine();
            partLine.setInvoice(invoice);
            partLine.setItemDescription(partRel.getPart().getName());
            partLine.setItemType(InvoiceItemType.PART);
            partLine.setQuantity(partRel.getQuantityRequired());
            partLine.setUnitPrice(partRel.getPart().getCurrentUnitPrice());
            partLine.calculateAmounts(); // ← THÊM: Tính lineTotal ngay
            lines.add(partLine);
        }

        return lines;
    }

    private List<InvoiceLineResponse> toLineResponses(List<InvoiceLine> lines) {
        return lines.stream().map(line -> InvoiceLineResponse.builder()
                        .id(line.getId())
                        .itemDescription(line.getItemDescription())
                        .itemType(line.getItemType())
                        .quantity(line.getQuantity())
                        .unitPrice(line.getUnitPrice())
                        .totalPrice(line.getLineTotal()) // ← SỬA: totalPrice (trong DTO) = lineTotal (trong Entity)
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