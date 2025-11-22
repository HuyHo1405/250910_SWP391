package com.example.demo.service.impl;

import com.example.demo.config.VnpayConfig;
import com.example.demo.exception.CommonException;
import com.example.demo.model.dto.PaymentRequest;
import com.example.demo.model.dto.PaymentResponse;
import com.example.demo.model.entity.Booking;
import com.example.demo.model.entity.Invoice;
import com.example.demo.model.entity.Payment;
import com.example.demo.model.modelEnum.BookingStatus;
import com.example.demo.model.modelEnum.InvoiceStatus;
import com.example.demo.model.modelEnum.PaymentMethod;
import com.example.demo.model.modelEnum.PaymentStatus;
import com.example.demo.repo.BookingRepo;
import com.example.demo.repo.InvoiceRepo;
import com.example.demo.repo.PaymentRepo;
import com.example.demo.service.interfaces.IPaymentService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // Tự động inject các dependency
@Slf4j // Dùng để log
public class PaymentService implements IPaymentService {

    private final PaymentRepo paymentRepository;
    private final InvoiceRepo invoiceRepository;
    private final VnpayConfig vnpayConfig;
    private final HttpServletRequest httpServletRequest; // Dùng để lấy IP
    private final AccessControlService accessControlService;
    private final BookingRepo bookingRepo;

    @Override
    @Transactional
    public PaymentResponse.PaymentURL createPayment(PaymentRequest.CreatePayment request) {
        log.info("Bắt đầu tạo yêu cầu thanh toán VNPAY cho hóa đơn {}", request.getInvoiceId());

        // 1. Tìm hóa đơn
        Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy hóa đơn với ID: " + request.getInvoiceId()));

        if(invoice.getStatus() == InvoiceStatus.PAID) {
            throw new CommonException.InvalidOperation("Hóa đơn đã được thanh toán trước đó.");
        }

        BigDecimal amount = invoice.getTotalAmount();
        log.info("VNPAY AMOUNT: {}", amount);

        // 2. Tạo một bản ghi Payment
        String orderCode = "VNP" + System.currentTimeMillis();
        log.info("VNPAY ORDER CODE: {}", orderCode);

        Payment payment = Payment.builder()
                .invoice(invoice)
                .paymentMethod(PaymentMethod.VNPAY)
                .amount(amount)
                .status(PaymentStatus.PENDING)
                .orderCode(orderCode)
                .build();
        paymentRepository.save(payment);

        // 3. Chuẩn bị các tham số cho VNPAY
        Map<String, String> vnpParams = new TreeMap<>();
        vnpParams.put("vnp_Version", VnpayConfig.VERSION);
        vnpParams.put("vnp_Command", VnpayConfig.COMMAND_PAY);
        vnpParams.put("vnp_TmnCode", vnpayConfig.getTmnCode());
        vnpParams.put("vnp_Amount", amount.multiply(new BigDecimal("100")).toBigInteger().toString());
        vnpParams.put("vnp_CurrCode", VnpayConfig.CURR_CODE);
        vnpParams.put("vnp_TxnRef", orderCode);
        vnpParams.put("vnp_OrderInfo", "Thanh toan don hang " + orderCode);
        vnpParams.put("vnp_OrderType", "other");
        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_ReturnUrl", vnpayConfig.getReturnUrl());

        String ipAddr = getIpAddress(httpServletRequest);
        vnpParams.put("vnp_IpAddr", ipAddr);
        log.info("VNPAY IP_ADDR: {}", ipAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        vnpParams.put("vnp_CreateDate", formatter.format(cld.getTime()));

        // Log all params
        log.info("VNPAY PARAMS: {}", vnpParams);

        // 4. Tạo Query String
        List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        for (String fieldName : fieldNames) {
            String fieldValue = vnpParams.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));

                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));

                hashData.append('&');
                query.append('&');
            }
        }

        String queryUrl = query.substring(0, query.length() - 1);
        String hashString = hashData.substring(0, hashData.length() - 1);

        log.info("VNPAY HASH STRING: {}", hashString);

        String secureHash = createVnpayHash(hashString, vnpayConfig.getHashSecret());

        log.info("VNPAY SECURE HASH: {}", secureHash);

        queryUrl += "&vnp_SecureHash=" + secureHash;

        String paymentUrl = vnpayConfig.getUrl() + "?" + queryUrl;

        log.info("VNPAY PAYMENT URL: {}", paymentUrl);

        return PaymentResponse.PaymentURL.builder()
                .paymentUrl(paymentUrl)
                .orderCode(orderCode)
                .build();
    }

    @Override
    @Transactional
    public PaymentResponse.VnpayIpn handleVnpayIpn(Map<String, String> vnpayParams) {
        log.info("Bắt đầu xử lý IPN từ VNPAY");

        try {
            // 1. Lấy vnp_SecureHash từ VNPAY
            String receivedHash = vnpayParams.get("vnp_SecureHash");
            vnpayParams.remove("vnp_SecureHash");

            // Sắp xếp lại và tạo chuỗi hash data
            Map<String, String> sortedParams = new TreeMap<>(vnpayParams);
            StringBuilder hashData = new StringBuilder();
            for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
                String fieldName = entry.getKey();
                String fieldValue = entry.getValue();
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                    hashData.append('&');
                }
            }
            hashData.deleteCharAt(hashData.length() - 1); // Xóa dấu & cuối

            // 2. Tính toán chữ ký
            String calculatedHash = createVnpayHash(hashData.toString(), vnpayConfig.getHashSecret());

            // 3. So sánh chữ ký
            if (!calculatedHash.equals(receivedHash)) {
                log.warn("IPN: Chữ ký không hợp lệ (Invalid Signature)");
                return new PaymentResponse.VnpayIpn("97", "Invalid Signature");
            }

            // 4. Lấy thông tin giao dịch
            String orderCode = vnpayParams.get("vnp_TxnRef");
            String vnpResponseCode = vnpayParams.get("vnp_ResponseCode");
            String vnpTransactionNo = vnpayParams.get("vnp_TransactionNo");
            String vnpAmount = vnpayParams.get("vnp_Amount"); // Số tiền * 100

            // 5. Tìm Payment trong DB
            Payment payment = paymentRepository.findByOrderCode(orderCode)
                    .orElse(null);

            if (payment == null) {
                log.warn("IPN: Không tìm thấy đơn hàng (Order not found): {}", orderCode);
                return new PaymentResponse.VnpayIpn("01", "Order not found");
            }

            // 6. Kiểm tra số tiền
            BigDecimal paymentAmountVNP = new BigDecimal(vnpAmount).divide(new BigDecimal("100"));
            if (payment.getAmount().compareTo(paymentAmountVNP) != 0) {
                log.warn("IPN: Số tiền không khớp (Invalid Amount) - DB: {} | VNPAY: {}", payment.getAmount(), paymentAmountVNP);
                return new PaymentResponse.VnpayIpn("04", "Invalid Amount");
            }

            // 7. Kiểm tra trạng thái
            if (payment.getStatus() != PaymentStatus.PENDING) {
                log.info("IPN: Đơn hàng đã được xử lý (Order already confirmed): {}", orderCode);
                return new PaymentResponse.VnpayIpn("02", "Order already confirmed");
            }

            // 8. Cập nhật trạng thái
            payment.setTransactionRef(vnpTransactionNo);
            payment.setResponseCode(vnpResponseCode);
            payment.setRawResponseData(vnpayParams.toString()); // Lưu lại toàn bộ response để debug


            if ("00".equals(vnpResponseCode)) {
                log.info("IPN: Thanh toán thành công (Success) cho đơn hàng {}", orderCode);

                payment.getInvoice().setStatus(InvoiceStatus.PAID);
                payment.getInvoice().setPaidAt(LocalDateTime.now());
                Invoice invoice = invoiceRepository.save(payment.getInvoice());


                Booking booking = invoice.getBooking();
                booking.setBookingStatus(BookingStatus.PAID);

                payment.setStatus(PaymentStatus.SUCCESSFUL);
                payment.setPaidAt(LocalDateTime.now());

                paymentRepository.save(payment);
                return new PaymentResponse.VnpayIpn("00", "Payment Success");
            } else {
                log.warn("IPN: Thanh toán thất bại (Failed) cho đơn hàng {}. Mã lỗi: {}", orderCode, vnpResponseCode);
                payment.setStatus(PaymentStatus.FAILED);

                paymentRepository.save(payment);
                return new PaymentResponse.VnpayIpn(vnpResponseCode, "Payment Failed");
            }

        } catch (Exception e) {
            log.error("IPN: Lỗi không xác định", e);
            return new PaymentResponse.VnpayIpn("99", "Unknown error");
        }
    }

    @Override
    public PaymentResponse.PaymentStatusDetail checkPaymentStatus(String orderCode) {
        log.info("Kiểm tra trạng thái thanh toán cho đơn hàng {}", orderCode);

        Payment payment = paymentRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy giao dịch với mã: " + orderCode));

        return PaymentResponse.PaymentStatusDetail.builder()
                .status(payment.getStatus())
                .responseCode(payment.getResponseCode())
                .message(mapStatusToMessage(payment.getStatus(), payment.getResponseCode()))
                .build();
    }

    @Override
    @Transactional
    public PaymentResponse.VnpayIpn simulateIpnSuccess(String orderCode) {
        log.warn("!!! BẮT ĐẦU CHẠY GIẢ LẬP IPN THÀNH CÔNG CHO ĐƠN HÀNG: {} !!!", orderCode);

        // 1. Tìm payment trong DB
        Payment payment = paymentRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new EntityNotFoundException("Test: Không tìm thấy " + orderCode));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            log.warn("Test: Đơn hàng {} đã được xử lý rồi (Status: {})", orderCode, payment.getStatus());
            return new PaymentResponse.VnpayIpn("02", "Order already confirmed");
        }

        // 2. Tạo Map<String, String> y hệt VNPAY sẽ gửi
        // Dùng TreeMap để nó tự sắp xếp theo A-Z
        Map<String, String> fakeVnpayParams = new TreeMap<>();
        fakeVnpayParams.put("vnp_Amount", payment.getAmount().multiply(new BigDecimal("100")).toBigInteger().toString());
        fakeVnpayParams.put("vnp_BankCode", "NCB"); // Ngân hàng test
        fakeVnpayParams.put("vnp_BankTranNo", "VNP987654"); // Số giao dịch giả
        fakeVnpayParams.put("vnp_CardType", "ATM");
        fakeVnpayParams.put("vnp_OrderInfo", "Thanh toan don hang " + orderCode);
        fakeVnpayParams.put("vnp_PayDate", "20251104113000"); // Thời gian giả
        fakeVnpayParams.put("vnp_ResponseCode", "00"); // 00 = Thành công
        fakeVnpayParams.put("vnp_TmnCode", vnpayConfig.getTmnCode());
        fakeVnpayParams.put("vnp_TransactionNo", "1234567"); // Số giao dịch VNPAY giả
        fakeVnpayParams.put("vnp_TransactionStatus", "00");
        fakeVnpayParams.put("vnp_TxnRef", orderCode); // Mã đơn hàng của bạn

        // 3. Tự tạo hashData (Vì dùng TreeMap nên đã tự sắp xếp A-Z)
        StringBuilder hashData = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : fakeVnpayParams.entrySet()) {
                String fieldName = entry.getKey();
                String fieldValue = entry.getValue();
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                    hashData.append('&');
                }
            }
            hashData.deleteCharAt(hashData.length() - 1); // Xóa dấu & cuối
        } catch (Exception e) {
            throw new RuntimeException("Lỗi tạo test hashData", e);
        }

        // 4. Tự tạo chữ ký HỢP LỆ bằng hàm private có sẵn
        String secureHash = createVnpayHash(hashData.toString(), vnpayConfig.getHashSecret());

        // Thêm chữ ký vào map
        fakeVnpayParams.put("vnp_SecureHash", secureHash);

        // 5. Gọi hàm IPN thật với dữ liệu giả lập chuẩn 100%
        log.info("Đang gọi hàm handleVnpayIpn với dữ liệu giả lập...");
        return handleVnpayIpn(fakeVnpayParams);
    }

    @Override
    @Transactional
    public PaymentResponse.VnpayIpn simulateIpnFail(String orderCode) {
        log.warn("!!! BẮT ĐẦU CHẠY GIẢ LẬP IPN THẤT BẠI CHO ĐƠN HÀNG: {} !!!", orderCode);

        // 1. Tìm payment trong DB
        Payment payment = paymentRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new EntityNotFoundException("Test: Không tìm thấy " + orderCode));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            log.warn("Test: Đơn hàng {} đã được xử lý rồi (Status: {})", orderCode, payment.getStatus());
            return new PaymentResponse.VnpayIpn("02", "Order already confirmed");
        }

        // 2. Tạo Map<String, String> y hệt VNPAY sẽ gửi khi THẤT BẠI
        // Dùng TreeMap để nó tự sắp xếp theo A-Z
        Map<String, String> fakeVnpayParams = new TreeMap<>();
        fakeVnpayParams.put("vnp_Amount", payment.getAmount().multiply(new BigDecimal("100")).toBigInteger().toString());
        fakeVnpayParams.put("vnp_BankCode", "NCB"); // Ngân hàng test
        fakeVnpayParams.put("vnp_BankTranNo", ""); // Không có số giao dịch khi thất bại
        fakeVnpayParams.put("vnp_CardType", "ATM");
        fakeVnpayParams.put("vnp_OrderInfo", "Thanh toan don hang " + orderCode);
        fakeVnpayParams.put("vnp_PayDate", "20251121153000"); // Thời gian giả

        // Các mã lỗi phổ biến của VNPAY:
        // "07" = Trừ tiền thành công. Giao dịch bị nghi ngờ (liên quan tới lừa đảo, giao dịch bất thường).
        // "09" = Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng chưa đăng ký dịch vụ InternetBanking tại ngân hàng.
        // "10" = Giao dịch không thành công do: Khách hàng xác thực thông tin thẻ/tài khoản không đúng quá 3 lần
        // "11" = Giao dịch không thành công do: Đã hết hạn chờ thanh toán. Xin quý khách vui lòng thực hiện lại giao dịch.
        // "12" = Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng bị khóa.
        // "13" = Giao dịch không thành công do Quý khách nhập sai mật khẩu xác thực giao dịch (OTP).
        // "24" = Giao dịch không thành công do: Khách hàng hủy giao dịch
        // "51" = Giao dịch không thành công do: Tài khoản của quý khách không đủ số dư để thực hiện giao dịch.
        // "65" = Giao dịch không thành công do: Tài khoản của Quý khách đã vượt quá hạn mức giao dịch trong ngày.
        // "75" = Ngân hàng thanh toán đang bảo trì.
        // "79" = Giao dịch không thành công do: KH nhập sai mật khẩu thanh toán quá số lần quy định.

        fakeVnpayParams.put("vnp_ResponseCode", "24"); // 24 = Khách hàng hủy giao dịch (phổ biến nhất)
        fakeVnpayParams.put("vnp_TmnCode", vnpayConfig.getTmnCode());
        fakeVnpayParams.put("vnp_TransactionNo", ""); // Không có số giao dịch khi thất bại
        fakeVnpayParams.put("vnp_TransactionStatus", "02"); // 02 = Thất bại
        fakeVnpayParams.put("vnp_TxnRef", orderCode); // Mã đơn hàng của bạn

        // 3. Tự tạo hashData (Vì dùng TreeMap nên đã tự sắp xếp A-Z)
        StringBuilder hashData = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : fakeVnpayParams.entrySet()) {
                String fieldName = entry.getKey();
                String fieldValue = entry.getValue();
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                    hashData.append('&');
                }
            }
            hashData.deleteCharAt(hashData.length() - 1); // Xóa dấu & cuối
        } catch (Exception e) {
            throw new RuntimeException("Lỗi tạo test hashData cho failed payment", e);
        }

        // 4. Tự tạo chữ ký HỢP LỆ bằng hàm private có sẵn
        String secureHash = createVnpayHash(hashData.toString(), vnpayConfig.getHashSecret());

        // Thêm chữ ký vào map
        fakeVnpayParams.put("vnp_SecureHash", secureHash);

        // 5. Gọi hàm IPN thật với dữ liệu giả lập chuẩn 100%
        log.info("Đang gọi hàm handleVnpayIpn với dữ liệu giả lập THẤT BẠI...");
        return handleVnpayIpn(fakeVnpayParams);
    }

    @Override
    public List<PaymentResponse.Transaction> getPaymentHistory(Long bookingId) {
        log.info("Bắt đầu lấy lịch sử thanh toán cho booking ID: {}", bookingId);

        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new CommonException.NotFound("Không tìm thấy booking với ID: " + bookingId));

        if(booking.getBookingStatus() != BookingStatus.PAID
            && booking.getBookingStatus() != BookingStatus.IN_PROGRESS
            && booking.getBookingStatus() != BookingStatus.MAINTENANCE_COMPLETE) {
            throw new CommonException.InvalidOperation("Chỉ có thể xem lịch sử thanh toán cho các booking đã thanh toán.");
        }

        accessControlService.verifyResourceAccess(booking.getCustomer().getId(), "PAYMENT", "view");

        // 1. Gọi PaymentRepo (sử dụng hàm bạn vừa thêm ở Bước 1)
        List<Payment> payments = paymentRepository.findByBookingId(bookingId);

        if (payments.isEmpty()) {
            log.info("Không tìm thấy lịch sử thanh toán nào cho booking ID: {}", bookingId);
            return Collections.emptyList();
        }

        // 2. Chuyển đổi (map) từ List<Payment> sang List<PaymentResponse.Transaction>
        return payments.stream()
                .map(this::toTransactionDTO) // Gọi hàm helper bên dưới
                .collect(Collectors.toList());
    }


    /**
     * Tạo chữ ký VNPAY (HMAC-SHA512)
     */
    private String createVnpayHash(String data, String secret) {
        try {
            Mac hmacSha512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.US_ASCII), "HmacSHA512");
            hmacSha512.init(secretKeySpec);
            byte[] hashBytes = hmacSha512.doFinal(data.getBytes(StandardCharsets.US_ASCII));

            // Chuyển byte array sang hex string
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            log.error("Lỗi khi tạo chữ ký VNPAY", e);
            throw new RuntimeException("Lỗi khi tạo chữ ký VNPAY", e);
        }
    }

    /**
     * Lấy địa chỉ IP của client
     */
    private String getIpAddress(HttpServletRequest request) {
        String ipAddr = request.getHeader("X-Forwarded-For");
        if (ipAddr == null || ipAddr.isEmpty() || "unknown".equalsIgnoreCase(ipAddr)) {
            ipAddr = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddr == null || ipAddr.isEmpty() || "unknown".equalsIgnoreCase(ipAddr)) {
            ipAddr = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddr == null || ipAddr.isEmpty() || "unknown".equalsIgnoreCase(ipAddr)) {
            ipAddr = request.getRemoteAddr();
            if ("0:0:0:0:0:0:0:1".equals(ipAddr) || "127.0.0.1".equals(ipAddr)) {
                // Hardcode IP cho môi trường test (sandbox VNPAY yêu cầu)
                ipAddr = "127.0.0.1"; // Hoặc một IP public nào đó của bạn
            }
        }
        return ipAddr;
    }

    /**
     * Chuyển trạng thái sang tin nhắn cho người dùng
     */
    private String mapStatusToMessage(PaymentStatus status, String responseCode) {
        switch (status) {
            case SUCCESSFUL:
                return "Giao dịch thành công (Mã: " + responseCode + ")";
            case PENDING:
                return "Giao dịch đang chờ thanh toán";
            case FAILED:
                return "Giao dịch thất bại (Mã: " + responseCode + ")";
            default:
                return "Không rõ trạng thái";
        }
    }

    private PaymentResponse.Transaction toTransactionDTO(Payment payment) {
        if (payment == null) {
            return null;
        }

        // Sử dụng DTO 'Transaction' có sẵn trong file PaymentResponse.java
        return PaymentResponse.Transaction.builder()
                .id(payment.getId())
                .invoiceNumber(payment.getInvoice() != null ? payment.getInvoice().getInvoiceNumber() : null)
                .orderCode(payment.getOrderCode())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .paymentMethod(payment.getPaymentMethod())
                .createdAt(payment.getCreatedAt())
                .paidAt(payment.getPaidAt())
                .transactionRef(payment.getTransactionRef())
                .responseCode(payment.getResponseCode())
                .build();
    }

}

