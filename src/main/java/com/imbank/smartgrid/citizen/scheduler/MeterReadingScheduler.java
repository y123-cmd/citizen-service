package com.imbank.smartgrid.citizen.scheduler;

import com.imbank.smartgrid.citizen.entity.Citizen;
import com.imbank.smartgrid.citizen.entity.MeterType;
import com.imbank.smartgrid.citizen.repository.CitizenRepository;
import com.imbank.smartgrid.citizen.service.CitizenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MeterReadingScheduler {
    private final CitizenRepository citizenRepository;
    private final CitizenService citizenService;
   @Scheduled(fixedRate = 30000)
    public void submitAutomatedReadings(){
       log.info("Scheduler triggered - submitting automated meter readings");
       List<Citizen> automatedCitizens = citizenRepository.findByMeterType(MeterType.AUTOMATED);
       log.info("Found {} automated citizens", automatedCitizens.size());
       for(Citizen citizen : automatedCitizens){
           try{
               citizenService.submitMeterReading(citizen.getCitizenId());
               log.info("Auto reading submitted for citizenId: {}",
                       citizen.getCitizenId());
           } catch (Exception e) {
               log.error("Failed auto reading for citizenId: {}",
                       citizen.getCitizenId(), e);
           }
       }
       log.info("Scheduler completed - processed {} automated citizens",
               automatedCitizens.size());
           }
}
