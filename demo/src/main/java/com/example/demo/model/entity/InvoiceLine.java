package com.example.demo.model.entity;

import com.example.demo.model.modelEnum.InvoiceItemType;
import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "item_description", nullable = false)
    private String itemDescription;

    @Column(name = "item_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private InvoiceItemType itemType; // 'SERVICE' or 'PART'

    @Column(name = "quantity", nullable = false)
    private Double quantity;

    @Column(name = "unit_price", nullable = false)
    private Double unitPrice;

    @Column(name = "line_total", nullable = false)
    private Double lineTotal;

    // Helper method to calculate all amounts
    public void calculateAmounts() {
        this.lineTotal = this.quantity * this.unitPrice;
    }

    @PrePersist
    @PreUpdate
    private void prePersistOrUpdate() {
        calculateAmounts();
    }
}