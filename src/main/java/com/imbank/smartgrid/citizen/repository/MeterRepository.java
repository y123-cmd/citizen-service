package com.imbank.smartgrid.citizen.repository;

import com.imbank.smartgrid.citizen.entity.Meter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MeterRepository extends JpaRepository<Meter, Long> {
    Optional<Meter> findByMeterId(String meterId);
    Optional<Meter> findByCitizenId(String citizenId);
}
