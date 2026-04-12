package com.imbank.smartgrid.citizen.Repository;

import com.imbank.smartgrid.citizen.entity.Citizen;
import com.imbank.smartgrid.citizen.entity.CitizenStatus;
import com.imbank.smartgrid.citizen.entity.MeterType;
import com.imbank.smartgrid.citizen.entity.ProviderName;
import com.imbank.smartgrid.citizen.repository.CitizenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class CitizenRepositoryTests {
    @Autowired
    private CitizenRepository citizenRepository;

    private Citizen testCitizen;

    @BeforeEach
    void setUp(){
        citizenRepository.deleteAll();
        testCitizen = new Citizen();
        testCitizen.setCitizenId("CIT-KPLC-00001");
        testCitizen.setName("John Doe");
        testCitizen.setProviderName(ProviderName.KPLC);
        testCitizen.setMeterType(MeterType.AUTOMATED);
        testCitizen.setMeterId("KPLC-SM-00001");
        testCitizen.setStatus(CitizenStatus.ACTIVE);

        citizenRepository.save(testCitizen);
    }
    @Test
    void findByCitizenId_WhenExists_ReturnsCitizen(){
        var result = citizenRepository.findByCitizenId("CIT-KPLC-00001");

        assertThat(result).isPresent();
        assertThat(result.get().getCitizenId()).isEqualTo("CIT-KPLC-00001");
        assertThat(result.get().getName()).isEqualTo("John Doe");
        assertThat(result.get().getProviderName()).isEqualTo(ProviderName.KPLC);
    }
    @Test
    void findByCitizenId_WhenNotFound_ReturnsEmpty(){
        var result = citizenRepository.findByCitizenId("CIT-KPLC-99999");
        assertThat(result).isEmpty();

    }
    @Test
    void findByProviderName_WhenExists_ReturnsCitizen(){
        Pageable pageable = PageRequest.of(0, 10);
        Page<Citizen> result = citizenRepository.findByProviderName(ProviderName.KPLC, pageable);

        assertThat(result.isEmpty()).isFalse();
        assertThat(result.getContent().size()).isEqualTo(1);
        assertThat(result.getContent().get(0).getProviderName()).isEqualTo(ProviderName.KPLC);
    }
    @Test
    void countByProviderName_ReturnsCorrectCount(){
        long count = citizenRepository.countByProviderName(ProviderName.KPLC);

        assertThat(count).isEqualTo(1L);
    }
    @Test
    void findByMeterType_WhenExists_ReturnsCitizens(){
        Pageable pageable = PageRequest.of(0,10);

        Page<Citizen> result = citizenRepository.findByMeterType(MeterType.AUTOMATED, pageable);

        assertThat(result.isEmpty()).isFalse();
        assertThat(result.getTotalElements()).isEqualTo(1L);
        assertThat(result.getContent().get(0).getMeterType()).isEqualTo(MeterType.AUTOMATED);

    }
    @Test
    void existsByCitizenId_WhenExists_ReturnsTrue() {
        boolean exists = citizenRepository.existsByCitizenId("CIT-KPLC-00001");

        assertThat(exists).isTrue();
    }

    @Test
    void existsByCitizenId_WhenNotExists_ReturnsFalse() {
        boolean exists = citizenRepository.existsByCitizenId("CIT-KPLC-99999");

        assertThat(exists).isFalse();
    }
    @Test
    void findByStatus_WhenExists_ReturnsCitizens() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Citizen> result = citizenRepository.findByStatus(CitizenStatus.ACTIVE, pageable);

        assertThat(result.isEmpty()).isFalse();
        assertThat(result.getTotalElements()).isEqualTo(1L);
        assertThat(result.getContent().get(0).getStatus()).isEqualTo(CitizenStatus.ACTIVE);
    }
    @Test
    void findByProviderNameAndMeterType_WhenExists_ReturnsCitizens() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Citizen> result = citizenRepository.findByProviderNameAndMeterType(
                ProviderName.KPLC, MeterType.AUTOMATED, pageable);

        assertThat(result.isEmpty()).isFalse();
        assertThat(result.getTotalElements()).isEqualTo(1L);
        assertThat(result.getContent().get(0).getProviderName()).isEqualTo(ProviderName.KPLC);
        assertThat(result.getContent().get(0).getMeterType()).isEqualTo(MeterType.AUTOMATED);
    }
}
