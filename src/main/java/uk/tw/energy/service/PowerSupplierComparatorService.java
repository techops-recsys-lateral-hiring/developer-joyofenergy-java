package uk.tw.energy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.PowerSupplier;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PowerSupplierComparatorService {

    private final List<PowerSupplier> powerSuppliers;
    private final MeterReadingService meterReadingService;
    private CostCalculationService costCalculationService;

    public PowerSupplierComparatorService(List<PowerSupplier> powerSuppliers, MeterReadingService meterReadingService) {
        this.powerSuppliers = powerSuppliers;
        this.meterReadingService = meterReadingService;
    }

    @Autowired
    public void setCostCalculationService(CostCalculationService costCalculationService) {
        this.costCalculationService = costCalculationService;
    }

    public Optional<Map<String, BigDecimal>> getPowerConsumptionCostForAllPlans(String smartMeterId) {
        Optional<List<ElectricityReading>> electricityReadings = meterReadingService.getReadings(smartMeterId);

        if (!electricityReadings.isPresent()) {
            return Optional.empty();
        }

        return Optional.of(powerSuppliers.stream().collect(
                Collectors.toMap(PowerSupplier::getPlanName, t -> costCalculationService.calculateCost(electricityReadings.get(), t))));
    }

}
