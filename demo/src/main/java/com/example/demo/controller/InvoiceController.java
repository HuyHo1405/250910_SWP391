package com.example.demo.controller;

import com.example.demo.model.dto.InvoiceRequest;
import com.example.demo.model.dto.InvoiceResponse;
import com.example.demo.service.interfaces.IInvoiceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
@Tag(name = "Invoice")
public class InvoiceController {

    private final IInvoiceService invoiceService;

    @PostMapping("/{bookingId}")
    public ResponseEntity<InvoiceResponse> createInvoice(@PathVariable Long bookingId) {
        InvoiceResponse response = invoiceService.create(bookingId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{invoiceId}")
    public ResponseEntity<InvoiceResponse> updateInvoice(
            @PathVariable Long invoiceId,
            @RequestBody InvoiceRequest.Update updateRequest
    ) {
        InvoiceResponse response = invoiceService.update(invoiceId, updateRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{invoiceId}")
    public ResponseEntity<InvoiceResponse> getInvoice(@PathVariable Long invoiceId) {
        InvoiceResponse response = invoiceService.findByBookingId(invoiceId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{invoiceId}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Long invoiceId) {
        invoiceService.deleteById(invoiceId);
        return ResponseEntity.noContent().build();
    }

}
