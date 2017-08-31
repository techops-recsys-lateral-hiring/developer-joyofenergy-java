package uk.tw.energy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.tw.energy.domain.MeterData;
import uk.tw.energy.service.MeterReadingService;

@RestController
@RequestMapping("/readings")
public class MeterReadingController {

    private MeterReadingService meterReadingService;

    public MeterReadingController(MeterReadingService meterReadingService) {

        this.meterReadingService = meterReadingService;

    }

    @PostMapping("/store")
    public ResponseEntity storeReadings(@RequestBody  MeterData meterData) {

        meterReadingService.storeReadings(meterData);
        return ResponseEntity.ok().build();

    }

}
