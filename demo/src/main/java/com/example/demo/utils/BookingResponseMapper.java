package com.example.demo.utils;

import com.example.demo.model.dto.BookingResponse;
import com.example.demo.model.dto.InvoiceLineResponse;
import com.example.demo.model.dto.InvoiceResponse;
import com.example.demo.model.dto.ScheduleDateTime;
import com.example.demo.model.entity.Booking;
import com.example.demo.model.entity.Invoice;
import com.example.demo.model.entity.InvoiceLine;
import com.example.demo.model.entity.Job;

import java.util.List;
import java.util.stream.Collectors;

public class BookingResponseMapper {
    private static final String DEFAULT_TIMEZONE = "Asia/Ho_Chi_Minh";
    private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    // === 1. MAPPER SƠ SƠ (CHO API DANH SÁCH) ===
    // Chỉ lấy thông tin Booking, KHÔNG lấy serviceDetails, KHÔNG lấy invoice.

    /**
     * Chuyển đổi Booking entity sang BookingResponse DTO (dạng tóm tắt).
     * Chỉ bao gồm thông tin cơ bản, không có chi tiết dịch vụ hay hóa đơn.
     * Dùng cho API lấy danh sách.
     */
    public static BookingResponse toDtoSummary(Booking booking) {
        return toDtoSummary(
                booking,
                ScheduleDateTimeParser.format(booking.getScheduleDate(), DATETIME_FORMAT, DEFAULT_TIMEZONE)
        );
    }

    public static BookingResponse toDtoSummary(Booking booking, ScheduleDateTime scheduleDateTime) {
        Job job = booking.getJob();


        return BookingResponse.builder()
                .id(booking.getId())
                .customerId(booking.getCustomer().getId())
                .customerName(booking.getCustomer().getFullName())
                .vehicleVin(booking.getVehicle().getVin())
                .vehicleModel(booking.getVehicle().getModel().getModelName())
                .scheduleDateTime(scheduleDateTime)
                .bookingStatus(booking.getBookingStatus() != null ? booking.getBookingStatus().name() : null)
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .assignedTechnicianId(job.getTechnician() != null? job.getTechnician().getId() : null)
                .assignedTechnicianName(job.getTechnician() != null? job.getTechnician().getFullName() : null)
                // Lưu ý: Ở đây chúng ta KHÔ

                // serviceDetails và invoice sẽ là null
                // và sẽ bị ẩn đi trong JSON nhờ @JsonInclude(JsonInclude.Include.NON_NULL)
                .build();
    }


    // === 2. MAPPER CHI TIẾT (BOOKING + SERVICE DETAILS) ===
    // (Đây là hàm "toDto" gốc của bạn, được đổi tên cho rõ nghĩa)

    /**
     * Chuyển đổi Booking entity sang BookingResponse DTO (dạng chi tiết).
     * Bao gồm thông tin cơ bản + danh sách chi tiết dịch vụ (BookingDetails).
     * KHÔNG bao gồm hóa đơn.
     */
    public static BookingResponse toDtoWithDetails(Booking booking) {
        return toDtoWithDetails(
                booking,
                ScheduleDateTimeParser.format(booking.getScheduleDate(), DATETIME_FORMAT, DEFAULT_TIMEZONE)
        );
    }

    public static BookingResponse toDtoWithDetails(Booking booking, ScheduleDateTime scheduleDateTime) {
        // 1. Lấy thông tin cơ bản (gọi hàm summary)
        BookingResponse response = toDtoSummary(booking, scheduleDateTime);

        // 2. Map chi tiết dịch vụ (BookingDetail)
        List<BookingResponse.CatalogDetail> serviceDetails = booking.getBookingDetails().stream()
                .map(detail -> BookingResponse.CatalogDetail.builder()
                        .id(detail.getId())
                        .catalogId(detail.getCatalogModel().getMaintenanceCatalog().getId()) // Giả định: detail.getCatalog()
                        .serviceName(detail.getCatalogModel().getMaintenanceCatalog().getName())
                        .description(detail.getDescription()) // Giả định: detail.getDescription()
                        .build())
                .collect(Collectors.toList());

        response.setCatalogDetails(serviceDetails);
        return response;
    }


    // === 3. MAPPER ĐẦY ĐỦ (CHO API CHI TIẾT) ===
    // Lấy Booking + Service Details + Invoice + Invoice Lines

    /**
     * Chuyển đổi Booking entity sang BookingResponse DTO (dạng đầy đủ).
     * Bao gồm thông tin cơ bản + chi tiết dịch vụ + Hóa đơn (và các dòng của hóa đơn).
     * Dùng cho API lấy chi tiết một Booking.
     */
    public static BookingResponse toDtoFull(Booking booking) {
        return toDtoFull(
                booking,
                ScheduleDateTimeParser.format(booking.getScheduleDate(), DATETIME_FORMAT, DEFAULT_TIMEZONE)
        );
    }

    public static BookingResponse toDtoFull(Booking booking, ScheduleDateTime scheduleDateTime) {
        // 1. Lấy Booking + Service Details (gọi hàm số 2)
        BookingResponse response = toDtoWithDetails(booking, scheduleDateTime);

        // 2. Kiểm tra và map Hóa đơn (Invoice) nếu có
        // (Giả định bạn đã thêm 'private Invoice invoice;' vào Booking.java)
        if (booking.getInvoice() != null) {
            Invoice invoice = booking.getInvoice();

            // 2a. Map các dòng trong hóa đơn (InvoiceLine)
            List<InvoiceLineResponse> lineResponses = invoice.getLines().stream()
                    .map(BookingResponseMapper::mapInvoiceLineToDto) // Gọi hàm helper bên dưới
                    .collect(Collectors.toList());

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

    /**
     * Hàm nội bộ để map 1 InvoiceLine (Entity) sang 1 InvoiceLineResponse (DTO).
     * (Dựa trên file InvoiceLineResponse.java bạn đã cung cấp)
     */
    private static InvoiceLineResponse mapInvoiceLineToDto(InvoiceLine line) {
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
