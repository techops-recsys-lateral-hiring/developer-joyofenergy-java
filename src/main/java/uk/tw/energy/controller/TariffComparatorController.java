package uk.tw.energy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.tw.energy.service.CostService;
import uk.tw.energy.service.TariffService;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/tariffs")
public class TariffComparatorController {

    private final CostService costService;
    private final TariffService tariffService;

    public TariffComparatorController(CostService costService, TariffService tariffService) {
        this.costService = costService;
        this.tariffService = tariffService;
    }

    @PostMapping("/compare-all/{meterId}")
    public ResponseEntity<Map<String, BigDecimal>> calculatedCostForEachTariff(@PathVariable String meterId) {
        return ResponseEntity.ok(tariffService.getConsumptionCostOfElectricityReadingsForEachTariff(meterId));
    }

}
