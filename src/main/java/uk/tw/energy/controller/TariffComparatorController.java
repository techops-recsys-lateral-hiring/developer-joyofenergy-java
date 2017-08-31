package uk.tw.energy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.tw.energy.domain.MeterData;
import uk.tw.energy.domain.Tariff;
import uk.tw.energy.service.CostService;
import uk.tw.energy.service.TariffService;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class TariffComparatorController {

    private final CostService costService;
    private final TariffService tariffService;

    public TariffComparatorController(CostService costService, TariffService tariffService) {
        this.costService = costService;
        this.tariffService = tariffService;
    }

    @PostMapping("/tariffs/compare-all")
    public ResponseEntity<List<BigDecimal>> calculateCostEndpoint(@RequestBody MeterData meterData) {
        Collection<Tariff> tariffList = tariffService.findAll();
        List<BigDecimal> collect = tariffList.stream().map(tariff -> costService.calculateCost(meterData, tariff))
                .collect(Collectors.toList());
        return ResponseEntity.ok(collect);
    }

}
