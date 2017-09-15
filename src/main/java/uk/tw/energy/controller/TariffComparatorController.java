package uk.tw.energy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.tw.energy.service.AccountService;
import uk.tw.energy.service.TariffService;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/tariffs")
public class TariffComparatorController {

    private final TariffService tariffService;
    private final AccountService accountService;

    public final static String TARIFF_ID_KEY = "tariff-id";
    public final static String TARIFF_COMPARISONS_KEY = "tariff-comparisons";

    public TariffComparatorController(TariffService tariffService, AccountService accountService) {
        this.tariffService = tariffService;
        this.accountService = accountService;
    }

    @GetMapping("/compare-all/{meterId}")
    public ResponseEntity<Map<String,Object>> calculatedCostForEachTariff(@PathVariable String meterId) {
        String tariffId = accountService.getTariffForMeter(meterId);
        Optional<Map<String, BigDecimal>> consumptionsForTariffs = tariffService.getConsumptionCostOfElectricityReadingsForEachTariff(meterId);

        if (!consumptionsForTariffs.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Map<String,Object> tariffComparisons = new HashMap<>();
        tariffComparisons.put(TARIFF_ID_KEY, tariffId);
        tariffComparisons.put(TARIFF_COMPARISONS_KEY, consumptionsForTariffs.get());

        return consumptionsForTariffs.isPresent()
                ? ResponseEntity.ok(tariffComparisons)
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/recommend/{meterId}")
    public ResponseEntity<List<Map.Entry<String,BigDecimal>>> recommendCheapestTariffs(@PathVariable String meterId, @RequestParam(value = "limit", required = false) Integer limit) {
        Optional<Map<String, BigDecimal>> consumptionsForTariffs = tariffService.getConsumptionCostOfElectricityReadingsForEachTariff(meterId);

        if (!consumptionsForTariffs.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        List<Map.Entry<String,BigDecimal>> recommendations = new ArrayList<>(consumptionsForTariffs.get().entrySet());
        recommendations.sort(Comparator.comparing(Map.Entry::getValue));

        if (limit != null && limit < recommendations.size()) {
            recommendations = recommendations.subList(0, limit);
        }

        return ResponseEntity.ok(recommendations);
    }
}
