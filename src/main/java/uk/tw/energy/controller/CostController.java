package uk.tw.energy.controller;

import java.math.BigDecimal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/costs")
public class CostController {

    // private final MeterReadingService meterReadingService;

    public CostController() {
        // this.meterReadingService = meterReadingService;
    }

    @GetMapping("/last-week/{smartMeterId}")
    public ResponseEntity<BigDecimal> getLastWeekCost(@PathVariable String smartMeterId) {
        //     Optional<List<ElectricityReading>> readings = meterReadingService.getReadings(smartMeterId);
        //     return readings.isPresent()
        //             ? ResponseEntity.ok(readings.get())
        //             : ResponseEntity.notFound().build();
        return ResponseEntity.ok(BigDecimal.valueOf(0L));
    }
}
