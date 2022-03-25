package uk.tw.energy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.tw.energy.service.AccountService;
import uk.tw.energy.service.PowerSupplierComparatorService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/price-plans")
public class PowerSupplierComparatorController {

    public final static String PRICE_PLAN_ID_KEY = "planId";
    public final static String PRICE_PLAN_COMPARISONS_KEY = "pricePlanComparisons";
    private final PowerSupplierComparatorService powerSupplierComparatorService;
    private final AccountService accountService;

    public PowerSupplierComparatorController(PowerSupplierComparatorService powerSupplierComparatorService, AccountService accountService) {
        this.powerSupplierComparatorService = powerSupplierComparatorService;
        this.accountService = accountService;
    }

    @GetMapping("/compare-all/{smartMeterId}")
    public ResponseEntity<Map<String, Object>> calculatedCostForEachPricePlan(@PathVariable String smartMeterId) {
        String planId = accountService.getPlanIdForSmartMeterId(smartMeterId);
        Optional<Map<String, BigDecimal>> consumptionCostForAllPlans =
                powerSupplierComparatorService.getPowerConsumptionCostForAllPlans(smartMeterId);

        if (!consumptionCostForAllPlans.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> pricePlanComparisons = new HashMap<>();
        pricePlanComparisons.put(PRICE_PLAN_ID_KEY, planId);
        pricePlanComparisons.put(PRICE_PLAN_COMPARISONS_KEY, consumptionCostForAllPlans.get());

        return ResponseEntity.ok(pricePlanComparisons);
    }

    @GetMapping("/recommend/{smartMeterId}")
    public ResponseEntity<List<Map.Entry<String, BigDecimal>>> recommendCheapestPlans(@PathVariable String smartMeterId,
                                                                                      @RequestParam(value = "limit", required = false) Integer limit) {
        Optional<Map<String, BigDecimal>> consumptionCostForAllPlans =
                powerSupplierComparatorService.getPowerConsumptionCostForAllPlans(smartMeterId);

        if (!consumptionCostForAllPlans.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        List<Map.Entry<String, BigDecimal>> recommendations = new ArrayList<>(consumptionCostForAllPlans.get().entrySet());
        recommendations.sort(Comparator.comparing(Map.Entry::getValue));

        if (limit != null && limit < recommendations.size()) {
            recommendations = recommendations.subList(0, limit);
        }

        return ResponseEntity.ok(recommendations);
    }
}
