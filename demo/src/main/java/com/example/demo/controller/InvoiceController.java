package com.example.demo.controller;

import com.example.demo.model.dto.InvoiceRequest;
import com.example.demo.model.dto.InvoiceResponse;
import com.example.demo.service.interfaces.IInvoiceService;
import io.swagger.v3.oas.annotations.Operation;
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

    @PutMapping("/{invoiceId}")
    @Operation(
        summary = "[PRIVATE] [OWNER/STAFF] Update invoice",
        description = "Updates an invoice by its id. Requires authentication as owner or staff."
    )
    public ResponseEntity<InvoiceResponse> updateInvoice(
            @PathVariable Long invoiceId,
            @RequestBody InvoiceRequest.UpdateInvoice updateRequest
    ) {
        InvoiceResponse response = invoiceService.updateById(invoiceId, updateRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{invoiceId}")
    @Operation(
        summary = "[PRIVATE] [OWNER/STAFF] Get invoice by id",
        description = "Returns an invoice by its id. Requires authentication as owner or staff."
    )
    public ResponseEntity<InvoiceResponse> getInvoice(@PathVariable Long invoiceId) {
        InvoiceResponse response = invoiceService.findById(invoiceId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{invoiceId}")
    @Operation(
        summary = "[PRIVATE] [OWNER/STAFF] Delete invoice",
        description = "Deletes an invoice by its id. Requires authentication as owner or staff."
    )
    public ResponseEntity<Void> deleteInvoice(@PathVariable Long invoiceId) {
        invoiceService.deleteById(invoiceId);
        return ResponseEntity.noContent().build();
    }

}
