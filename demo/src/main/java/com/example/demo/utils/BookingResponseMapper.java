package com.example.demo.utils;

import com.example.demo.model.dto.BookingResponse;
import com.example.demo.model.dto.InvoiceLineResponse;
import com.example.demo.model.dto.InvoiceResponse;
import com.example.demo.model.dto.ScheduleDateTime;
import com.example.demo.model.entity.*; // SỬA: Import thêm các entity
import com.example.demo.model.entity.Vehicle; // (Giả định)
import com.example.demo.model.entity.VehicleModel; // (Giả định)
import com.example.demo.model.entity.MaintenanceCatalog; // (Giả định)
import com.example.demo.model.entity.MaintenanceCatalogModel; // (Giả định)

import java.util.Collections; // SỬA: Import Collections
import java.util.List;
import java.util.stream.Collectors;

public class BookingResponseMapper {
    private static final String DEFAULT_TIMEZONE = "Asia/Ho_Chi_Minh";
    private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    // === 1. MAPPER SƠ SƠ (CHO API DANH SÁCH) ===

    public static BookingResponse toDtoSummary(Booking booking) {
        return toDtoSummary(
                booking,
                ScheduleDateTimeParser.format(booking.getScheduleDate(), DATETIME_FORMAT, DEFAULT_TIMEZONE)
        );
    }

    public static BookingResponse toDtoSummary(Booking booking, ScheduleDateTime scheduleDateTime) {
        // === SỬA LỖI NPE (Null-safe checks) ===
        // Tách các đối tượng con ra và kiểm tra null trước khi dùng
        Job job = booking.getJob();
        User technician = (job != null) ? job.getTechnician() : null;
        Vehicle vehicle = booking.getVehicle();
        VehicleModel model = (vehicle != null) ? vehicle.getModel() : null;
        User customer = booking.getCustomer();

        return BookingResponse.builder()
                .id(booking.getId())
                // Kiểm tra customer (dù là not-null, nhưng đây là cách an toàn)
                .customerId(customer != null ? customer.getId() : null)
                .customerName(customer != null ? customer.getFullName() : null)
                // Kiểm tra vehicle
                .vehicleVin(vehicle != null ? vehicle.getVin() : null)
                // Kiểm tra vehicle -> model
                .vehicleModel(model != null ? model.getModelName() : null)
                .scheduleDateTime(scheduleDateTime)
                .bookingStatus(booking.getBookingStatus() != null ? booking.getBookingStatus().name() : null)
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                // Kiểm tra technician (job -> technician)
                .assignedTechnicianId(technician != null ? technician.getId() : null)
                .assignedTechnicianName(technician != null ? technician.getFullName() : null)
                .build();
    }


    // === 2. MAPPER CHI TIẾT (BOOKING + SERVICE DETAILS) ===

    public static BookingResponse toDtoWithDetails(Booking booking) {
        return toDtoWithDetails(
                booking,
                ScheduleDateTimeParser.format(booking.getScheduleDate(), DATETIME_FORMAT, DEFAULT_TIMEZONE)
        );
    }

    public static BookingResponse toDtoWithDetails(Booking booking, ScheduleDateTime scheduleDateTime) {
        // 1. Lấy thông tin cơ bản (đã được làm an toàn ở trên)
        BookingResponse response = toDtoSummary(booking, scheduleDateTime);

        // 2. Map chi tiết dịch vụ (BookingDetail)
        // SỬA: Thêm kiểm tra null cho danh sách (dù đã init, đây là cách phòng vệ tốt)
        List<BookingResponse.CatalogDetail> serviceDetails = Collections.emptyList();
        if (booking.getBookingDetails() != null) {
            serviceDetails = booking.getBookingDetails().stream()
                    .map(detail -> {
                        // === SỬA LỖI NPE (Null-safe checks) ===
                        // Kiểm tra chuỗi truy cập: detail -> catalogModel -> maintenanceCatalog
                        MaintenanceCatalogModel catalogModel = detail.getCatalogModel();
                        MaintenanceCatalog catalog = (catalogModel != null) ? catalogModel.getMaintenanceCatalog() : null;

                        return BookingResponse.CatalogDetail.builder()
                                .id(detail.getId())
                                .catalogId(catalog != null ? catalog.getId() : null) // An toàn
                                .serviceName(catalog != null ? catalog.getName() : null) // An toàn
                                .description(detail.getDescription())
                                .build();
                    })
                    .collect(Collectors.toList());
        }

        response.setCatalogDetails(serviceDetails);
        return response;
    }


    // === 3. MAPPER ĐẦY ĐỦ (CHO API CHI TIẾT) ===

    public static BookingResponse toDtoFull(Booking booking) {
        return toDtoFull(
                booking,
                ScheduleDateTimeParser.format(booking.getScheduleDate(), DATETIME_FORMAT, DEFAULT_TIMEZONE)
        );
    }

    public static BookingResponse toDtoFull(Booking booking, ScheduleDateTime scheduleDateTime) {
        // 1. Lấy Booking + Service Details (đã được làm an toàn ở trên)
        BookingResponse response = toDtoWithDetails(booking, scheduleDateTime);

        // 2. Kiểm tra và map Hóa đơn (Invoice) nếu có
        Invoice invoice = booking.getInvoice(); // An toàn (có thể null)
        if (invoice != null) {

            // 2a. Map các dòng trong hóa đơn (InvoiceLine)
            // SỬA: Thêm kiểm tra null cho danh sách (dù đã init)
            List<InvoiceLineResponse> lineResponses = Collections.emptyList();
            if (invoice.getLines() != null) {
                lineResponses = invoice.getLines().stream()
                        .map(BookingResponseMapper::mapInvoiceLineToDto)
                        .collect(Collectors.toList());
            }

            // 2b. Map thông tin Invoice
            InvoiceResponse invoiceResponse = InvoiceResponse.builder()
                    .id(invoice.getId())
                    .invoiceNumber(invoice.getInvoiceNumber())
                    .issueDate(invoice.getIssueDate())
                    .dueDate(invoice.getDueDate())
                    .totalAmount(invoice.getTotalAmount())
                    .status(invoice.getStatus())
                    .createdAt(invoice.getCreatedAt())
                    .invoiceLines(lineResponses)
                    .build();

            // 3. Gán hóa đơn vào BookingResponse
            response.setInvoice(invoiceResponse);
        }

        return response;
    }


    // === HÀM HELPER (RIÊNG TƯ) ===

    private static InvoiceLineResponse mapInvoiceLineToDto(InvoiceLine line) {
        // SỬA: Thêm kiểm tra đầu vào (đề phòng)
        if (line == null) {
            return null;
        }
        return InvoiceLineResponse.builder()
                .id(line.getId())
                .itemDescription(line.getItemDescription())
                .itemType(line.getItemType())
                .quantity(line.getQuantity())
                .unitPrice(line.getUnitPrice())
                .totalPrice(line.getLineTotal())
                .build();
    }
}