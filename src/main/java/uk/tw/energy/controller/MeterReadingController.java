package uk.tw.energy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import uk.tw.energy.domain.MeterData;
import uk.tw.energy.service.MeterReadingService;

public class MeterReadingController {

    private MeterReadingService meterReadingService;

    public MeterReadingController(MeterReadingService meterReadingService) {

        this.meterReadingService = meterReadingService;

    }

    public ResponseEntity storeReading(@RequestBody  MeterData meterData) {

        meterReadingService.storeReadings(meterData);
        return ResponseEntity.ok().build();

    }

}
