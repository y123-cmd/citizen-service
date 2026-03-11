package com.imbank.smartgrid.citizen.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "citizen")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Citizen {
    @Id
    @Column(name = "citizen_id",length = 20)
    private String citizenId;
    @Column(name = "first_name",nullable = false,length = 50)
    private String firstName;
    @Column(name = "last_name",nullable =false,length = 50)
    private String lastName;
    @Column(nullable = false,unique = true,length = 100)
    private String email;
    @Column(name = "phone_number",nullable = false,length = 20)
    private String phoneNumber;
    @Enumerated(EnumType.STRING)
    @Column(name = "provider_name",nullable = false,length = 20)
    private ProviderName providerName;
    @Column(name = "created_at",nullable = false,updatable = false)
    private LocalDateTime createdAt;
    @PrePersist
    protected void onCreate(){
        createdAt = LocalDateTime.now();
    }
}
