package com.example.demo.service.interfaces;

import com.example.demo.model.dto.InvoiceRequest;
import com.example.demo.model.dto.InvoiceResponse;
import com.example.demo.model.modelEnum.InvoiceStatus;

public interface IInvoiceService {
    InvoiceResponse create(Long bookingId);
    InvoiceResponse updateById(Long invoiceId, InvoiceRequest.UpdateInvoice update);
    InvoiceResponse findById(Long id);
    void deleteById(Long invoiceId);
}
