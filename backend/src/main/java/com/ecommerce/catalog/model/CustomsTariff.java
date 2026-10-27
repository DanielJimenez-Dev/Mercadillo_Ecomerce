package com.ecommerce.catalog.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "customs_tariffs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomsTariff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hs_code", nullable = false, unique = true, length = 20)
    private String hsCode;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(name = "duty_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal dutyRate;

    @Column(name = "vat_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal vatRate;

    @Column(name = "requires_permit")
    @Builder.Default
    private Boolean requiresPermit = false;
}
