package uk.tw.energy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.tw.energy.service.TariffService;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/tariffs")
public class TariffComparatorController {

    private final TariffService tariffService;

    public TariffComparatorController(TariffService tariffService) {
        this.tariffService = tariffService;
    }

    @GetMapping("/compare-all/{meterId}")
    public ResponseEntity<Map<String, BigDecimal>> calculatedCostForEachTariff(@PathVariable String meterId) {
        return ResponseEntity.ok(tariffService.getConsumptionCostOfElectricityReadingsForEachTariff(meterId));
    }

}
