package uk.tw.energy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.tw.energy.service.TariffService;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/tariffs")
public class TariffComparatorController {

    private final TariffService tariffService;

    public TariffComparatorController(TariffService tariffService) {
        this.tariffService = tariffService;
    }

    @GetMapping("/compare-all/{meterId}")
    public ResponseEntity<Map<String, BigDecimal>> calculatedCostForEachTariff(@PathVariable String meterId) {

        Optional<Map<String, BigDecimal>> consumptionsForTariffs = tariffService.getConsumptionCostOfElectricityReadingsForEachTariff(meterId);

        if ( consumptionsForTariffs.isPresent() ) {

            return ResponseEntity.ok(consumptionsForTariffs.get());

        }

        return ResponseEntity.notFound().build();

    }

}
