package com.imbank.smartgrid.citizen.Repository;

import com.imbank.smartgrid.citizen.entity.Meter;
import com.imbank.smartgrid.citizen.entity.MeterType;
import com.imbank.smartgrid.citizen.entity.ProviderName;
import com.imbank.smartgrid.citizen.repository.MeterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@ActiveProfiles("tests")
public class MeterRepositoryTests {
    @Autowired
    private MeterRepository meterRepository;

    private Meter testMeter;

    @BeforeEach
    void setUp() {
        meterRepository.deleteAll();

        testMeter = new Meter();
        testMeter.setMeterId("KPLC-SM-00001");
        testMeter.setCitizenId("CIT-KPLC-00001");
        testMeter.setProviderName(ProviderName.KPLC);
        testMeter.setMeterType(MeterType.AUTOMATED);

        meterRepository.save(testMeter);
    }
    @Test
    void findByMeterId_WhenExists_ReturnsMeter() {
        var result = meterRepository.findByMeterId("KPLC-SM-00001");

        assertThat(result).isPresent();
        assertThat(result.get().getMeterId()).isEqualTo("KPLC-SM-00001");
        assertThat(result.get().getCitizenId()).isEqualTo("CIT-KPLC-00001");
    }

    @Test
    void findByMeterId_WhenNotExists_ReturnsEmpty() {
        var result = meterRepository.findByMeterId("KPLC-SM-99999");

        assertThat(result).isEmpty();
    }

    @Test
    void findByCitizenId_WhenExists_ReturnsMeter() {
        var result = meterRepository.findByCitizenId("CIT-KPLC-00001");

        assertThat(result).isPresent();
        assertThat(result.get().getCitizenId()).isEqualTo("CIT-KPLC-00001");
        assertThat(result.get().getProviderName()).isEqualTo(ProviderName.KPLC);
    }

    @Test
    void findByCitizenId_WhenNotExists_ReturnsEmpty() {
        var result = meterRepository.findByCitizenId("CIT-KPLC-99999");

        assertThat(result).isEmpty();
    }
}
