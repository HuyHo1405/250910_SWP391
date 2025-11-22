package com.example.demo.service.impl;

import com.example.demo.exception.CommonException; // ✅ Import
import com.example.demo.model.dto.InvoiceLineResponse;
import com.example.demo.model.dto.InvoiceRequest;
import com.example.demo.model.dto.InvoiceResponse;
import com.example.demo.model.entity.*;
import com.example.demo.model.modelEnum.BookingStatus;
import com.example.demo.model.modelEnum.InvoiceItemType;
import com.example.demo.model.modelEnum.InvoiceStatus;
import com.example.demo.repo.*;
import com.example.demo.service.interfaces.IInvoiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
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

        accessControlService.verifyResourceAccess(booking.getCustomer().getId(), "INVOICE", "create");
    
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

        // Set lines vào invoice
        invoice.setLines(lines);

        // Tính tổng tiền
        BigDecimal totalAmount = lines.stream()
                .map(line -> line.getQuantity().multiply(line.getUnitPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        invoice.setTotalAmount(totalAmount);

        // ✅ QUAN TRỌNG: Set bidirectional relationship
        booking.setInvoice(invoice);

        // Save invoice (sẽ cascade save lines)
        Invoice savedInvoice = invoiceRepo.save(invoice);

        // Save booking để update relationship
        bookingRepo.save(booking);

        log.info("Invoice created successfully with {} lines, total: {}", lines.size(), totalAmount);

        return mapToResponse(savedInvoice);
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
        accessControlService.verifyResourceAccess(invoice.getBooking().getCustomer().getId(), "INVOICE", "read");
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

    // 5. Update invoice khi booking thay đổi (thêm/xóa dịch vụ)
    @Override
    public InvoiceResponse updateInvoiceFromBooking(Long bookingId) {
        log.info("Updating invoice for booking {}", bookingId);

        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new CommonException.NotFound("Booking", bookingId));

        Invoice invoice = invoiceRepo.findByBookingId(bookingId)
                .orElseThrow(() -> new CommonException.NotFound("Invoice for Booking", bookingId));

        // Không cho update nếu invoice đã thanh toán
        if(invoice.getStatus() == InvoiceStatus.PAID) {
            throw new CommonException.InvalidOperation("Không thể cập nhật hóa đơn đã thanh toán");
        }

        // Xóa toàn bộ invoice lines cũ
        invoice.getLines().clear();

        // Tạo lại invoice lines từ booking details hiện tại
        List<InvoiceLine> newLines = new ArrayList<>();
        for (BookingDetail detail : booking.getBookingDetails()) {
            MaintenanceCatalogModel catalogModel = detail.getCatalogModel();
            newLines.addAll(buildInvoiceLine(catalogModel.getId(), invoice));
        }

        invoice.getLines().addAll(newLines);

        // Tính lại tổng tiền
        BigDecimal totalAmount = newLines.stream()
                .map(line -> line.getQuantity().multiply(line.getUnitPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        invoice.setTotalAmount(totalAmount);

        invoiceRepo.save(invoice);

        log.info("Invoice updated successfully for booking {}, new total: {}", bookingId, totalAmount);
        return mapToResponse(invoice);
    }

    @Scheduled(cron = "0 0 * * * *") // Chạy mỗi giờ
    @Transactional // Đảm bảo tất cả cùng thành công hoặc thất bại
    public void cancelOverdueInvoices() {
        log.info("[Scheduler] Bắt đầu chạy tác vụ: Hủy hóa đơn DRAFT quá hạn...");

        // 1. Lấy thời điểm hiện tại
        LocalDateTime now = LocalDateTime.now();

        // 2. Tìm tất cả hóa đơn DRAFT đã quá hạn (dueDate < now)
        List<Invoice> overdueInvoices = invoiceRepo.findByStatusAndDueDateBefore(
                InvoiceStatus.DRAFT,
                now
        );

        if (overdueInvoices.isEmpty()) {
            log.info("[Scheduler] Không tìm thấy hóa đơn DRAFT nào quá hạn.");
            return;
        }

        log.warn("[Scheduler] Tìm thấy {} hóa đơn quá hạn. Đang tiến hành hủy...", overdueInvoices.size());

        // 3. Chuyển trạng thái của chúng thành CANCELLED
        for (Invoice invoice : overdueInvoices) {
            invoice.setStatus(InvoiceStatus.CANCELLED);

            Booking booking = invoice.getBooking();
            booking.setBookingStatus(BookingStatus.CANCELLED);
            bookingRepo.save(booking);
            // (Tùy chọn) Bạn có thể muốn cập nhật lại booking hoặc không,
            // tùy theo logic nghiệp vụ của bạn.
            // Ví dụ: booking.setStatus(BookingStatus.CANCELLED);

            log.warn("[Scheduler] Hóa đơn ID {} (Booking ID: {}) đã quá hạn và bị hủy.",
                    invoice.getId(), invoice.getBooking().getId());
        }

        // 4. Lưu tất cả thay đổi vào DB
        invoiceRepo.saveAll(overdueInvoices);

        log.info("[Scheduler] Hoàn thành tác vụ. Đã hủy {} hóa đơn.", overdueInvoices.size());
    }

    // 6. Build InvoiceLine cho 1 dịch vụ & model (Catalog + Model)
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