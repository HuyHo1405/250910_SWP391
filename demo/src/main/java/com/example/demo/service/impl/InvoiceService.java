package com.example.demo.service.impl;

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
                .orElseThrow(() -> new RuntimeException("Không tìm thấy booking"));
        if (invoiceRepo.findByBookingId(bookingId).isPresent())
            throw new RuntimeException("Booking đã có hóa đơn!");

        Invoice invoice = new Invoice();
        invoice.setBooking(booking);
        invoice.setIssueDate(LocalDateTime.now());
        invoice.setStatus(InvoiceStatus.DRAFT);

        List<InvoiceLine> lines = new ArrayList<>();

        // Duyệt toàn bộ bookingDetail (dịch vụ đặt)
        for (BookingDetail detail : booking.getBookingDetails()) {
            Long catalogId = detail.getService().getId();
            Long modelId = booking.getVehicle().getModel().getId();

            lines.addAll(buildInvoiceLine(catalogId, modelId, invoice));
        }
        invoice.setLines(lines);
        invoiceRepo.save(invoice);

        return mapToResponse(invoice);
    }

    // 2. Cập nhật invoice (trả về DTO đã chỉnh)
    @Override
    public InvoiceResponse update(Long bookingId, InvoiceRequest.Update update) {
        Invoice invoice = invoiceRepo.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy invoice"));
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
    public InvoiceResponse updateStatus(Long bookingId, InvoiceStatus status) {
        Invoice invoice = invoiceRepo.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy invoice"));

        invoice.setStatus(status);
        return  mapToResponse(invoiceRepo.save(invoice));
    }

    // 3. Tìm invoice theo id (trả về DTO)
    @Override
    public InvoiceResponse findByBookingId(Long bookingId) {
        Invoice invoice = invoiceRepo.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy invoice"));
        return mapToResponse(invoice);
    }

    // 4. Xóa invoice theo id
    @Override
    public void deleteById(Long invoiceId) {
        Invoice invoice = invoiceRepo.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy invoice để xóa"));
        invoiceRepo.delete(invoice);
        // Có thể trả về boolean hoặc không return gì
    }

    // 5. Build InvoiceLine cho 1 dịch vụ & model (Catalog + Model)
    public List<InvoiceLine> buildInvoiceLine(Long catalogId, Long modelId, Invoice invoice){
        MaintenanceCatalog catalog = catalogRepo.findById(catalogId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy catalog"));
        MaintenanceCatalogModel selectedModel = null;
        for (MaintenanceCatalogModel model : catalog.getModels()) {
            if ((modelId).equals(model.getVehicleModel().getId())) {
                selectedModel = model;
                break;
            }
        }
        if (selectedModel == null)
            throw new RuntimeException("Không tìm thấy model phù hợp trong catalog");

        List<InvoiceLine> lines = new ArrayList<>();
        // 1. Dịch vụ chính - mỗi model tương ứng 1 dịch vụ
        InvoiceLine serviceLine = new InvoiceLine();
        serviceLine.setInvoice(invoice);
        serviceLine.setItemDescription(catalog.getName());
        serviceLine.setItemType(InvoiceItemType.SERVICE);
        serviceLine.setQuantity(1.0); // cập nhật quantity nếu business cần
        serviceLine.setUnitPrice(selectedModel.getMaintenancePrice());
        lines.add(serviceLine);

        // 2. Các phụ tùng thuộc model này
        for (MaintenanceCatalogModelPart partRel : catalogModelPartRepo.findByMaintenanceCatalogIdAndVehicleModelId(catalogId, modelId)) {
            InvoiceLine partLine = new InvoiceLine();
            partLine.setInvoice(invoice);
            partLine.setItemDescription(partRel.getPart().getName());
            partLine.setItemType(InvoiceItemType.PART);
            partLine.setQuantity((double) partRel.getQuantityRequired());
            partLine.setUnitPrice(partRel.getPart().getCurrentUnitPrice());
            lines.add(partLine);
        }

        return lines;
    }

    // Ví dụ có class InvoiceLineResponse riêng
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
