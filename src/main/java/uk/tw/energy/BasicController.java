package uk.tw.energy;

import org.springframework.http.ResponseEntity;
import uk.tw.energy.domain.MeterData;
import uk.tw.energy.domain.Tariff;
import uk.tw.energy.service.CostService;
import uk.tw.energy.service.TariffService;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class BasicController {
    private final CostService costService;
    private final TariffService tariffService;

    public BasicController(CostService costService, TariffService tariffService) {

        this.costService = costService;
        this.tariffService = tariffService;
    }

    public ResponseEntity<List<BigDecimal>> calculateCostEndpoint(MeterData meterData) {
        Collection<Tariff> tariffList = tariffService.findAll();
        List<BigDecimal> collect = tariffList.stream().map(tariff -> costService.calculateCost(meterData, tariff))
                .collect(Collectors.toList());
        return ResponseEntity.ok(collect);

    }
}
