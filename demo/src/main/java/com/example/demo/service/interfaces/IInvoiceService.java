package com.example.demo.service.interfaces;

import com.example.demo.model.dto.InvoiceRequest;
import com.example.demo.model.dto.InvoiceResponse;
import com.example.demo.model.modelEnum.InvoiceStatus;

public interface IInvoiceService {
    InvoiceResponse create(Long bookingId);
    InvoiceResponse update(Long invoiceId, InvoiceRequest.Update update);
    InvoiceResponse recalculateAndFinalize(Long bookingId);
    InvoiceResponse findByBookingId(Long bookingId);
    void deleteById(Long invoiceId);
}
