package uk.tw.energy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.tw.energy.service.AccountService;
import uk.tw.energy.service.TariffService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    public ResponseEntity<List<Map.Entry<String,BigDecimal>>> recommendCheapestTariffs(String meterId) {
        return ResponseEntity.ok().build();
    }
}
