package com.imbank.smartgrid.citizen.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "meter_state")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeterState {
    @Id
    @Column(name = "meter_id",length = 20)
    private String citizenId;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider_name", nullable = false, length = 20)
    private ProviderName providerName;

    @Column(name = "last_reading_kwh", nullable = false, precision = 10, scale = 2)
    private BigDecimal lastReadingKwh;

    @Column(name = "last_reading_date", nullable = false)
    private LocalDateTime lastReadingDate;

    @Column(name = "is_smart_meter", nullable = false)
    private Boolean isSmartMeter;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

