package uk.tw.energy.controller;

import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.tw.energy.domain.CustomResponse;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.MeterReadings;
import uk.tw.energy.service.MeterReadingService;

@RestController
@RequestMapping("/readings")
public class MeterReadingController {

    private final MeterReadingService meterReadingService;

    public MeterReadingController(MeterReadingService meterReadingService) {
        this.meterReadingService = meterReadingService;
    }

    @PostMapping("/store")
    public ResponseEntity<CustomResponse<String>> storeReadings(@RequestBody MeterReadings meterReadings) {
        if (ValidatorUtil.validateMeterReading((meterReadings))) {
            CustomResponse<String> response = CustomResponse.<String>builder()
                    .error("smartMeterId|electricityReadings is null or empty")
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .build();
            return ResponseEntity.badRequest().body(response);
        }
        meterReadingService.storeReadings(meterReadings.smartMeterId(), meterReadings.electricityReadings());
        return ResponseEntity.ok(CustomResponse.<String>builder()
                .payload("Records created!!!")
                .httpStatus(HttpStatus.CREATED)
                .build());
    }

    @GetMapping("/read/{smartMeterId}")
    public ResponseEntity<CustomResponse<List<ElectricityReading>>> readReadings(@PathVariable String smartMeterId) {
        Optional<List<ElectricityReading>> readingsOptional = meterReadingService.getReadings(smartMeterId);
        return readingsOptional
                .map(electricityReadings -> ResponseEntity.ok(CustomResponse.<List<ElectricityReading>>builder()
                        .payload(electricityReadings)
                        .httpStatus(HttpStatus.OK)
                        .build()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
