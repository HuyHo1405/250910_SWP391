package com.example.demo.model.entity;

import com.example.demo.model.modelEnum.InvoiceItemType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "invoice_lines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @Column(name = "item_description", nullable = false, columnDefinition = "NVARCHAR(255)")
    private String itemDescription;

    @Column(name = "item_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private InvoiceItemType itemType; // 'SERVICE' or 'PART'

    @Column(name = "quantity", nullable = false)
    private BigDecimal quantity;

    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;

    @Column(name = "line_total", nullable = false)
    private BigDecimal lineTotal;

    // Helper method to calculate all amounts
    public void calculateAmounts() {
        // --- THAY ĐỔI 2: Dùng .multiply() ---

        // Đảm bảo quantity và unitPrice không bị null
        if (this.quantity == null) {
            this.quantity = BigDecimal.ZERO;
        }
        if (this.unitPrice == null) {
            this.unitPrice = BigDecimal.ZERO;
        }

        // Dùng .multiply() thay vì dấu *
        this.lineTotal = this.quantity.multiply(this.unitPrice);
    }

    @PrePersist
    @PreUpdate
    private void prePersistOrUpdate() {
        calculateAmounts();
    }
}