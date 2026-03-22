package com.imbank.smartgrid.citizen.repository;

import com.imbank.smartgrid.citizen.entity.Citizen;
import com.imbank.smartgrid.citizen.entity.CitizenStatus;
import com.imbank.smartgrid.citizen.entity.MeterType;
import com.imbank.smartgrid.citizen.entity.ProviderName;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CitizenRepository extends JpaRepository<Citizen,Long> {
    Optional<Citizen> findByCitizenId(String citizenId);
    Page<Citizen>findByProviderName(ProviderName providerName, Pageable pageable);
    Page<Citizen>findByMeterType(MeterType meterType,Pageable pageable);
    Page<Citizen>findByStatus(CitizenStatus status,Pageable pageable);
    Page<Citizen>findByProviderNameAndMeterType(ProviderName providerName, MeterType meterType,Pageable pageable);
    boolean existsByCitizenId(String citizenId);
    long countByProviderName(ProviderName providerName);
}
