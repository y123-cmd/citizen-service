package com.imbank.smartgrid.citizen.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "meter")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Meter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true,length = 20)
    private String meterId;

    @Column(nullable = false,unique = true,length = 20)
    private String citizenId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProviderName providerName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MeterType meterType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CitizenStatus status;

    @Column(precision = 10, scale = 2)
    private BigDecimal lastReading;

    @Column(nullable = false,updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected  void onCreate(){
        createdAt = LocalDateTime.now();
        if (status == null) status = CitizenStatus.ACTIVE;
        if(lastReading == null) lastReading = BigDecimal.ZERO;

    }
}
