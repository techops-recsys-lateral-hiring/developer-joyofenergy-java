package uk.tw.energy.service;

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

    private final CostCalculationService costCalculationService;

    public PowerSupplierComparatorService(List<PowerSupplier> powerSuppliers, MeterReadingService meterReadingService, CostCalculationService costCalculationService) {
        this.powerSuppliers = powerSuppliers;
        this.meterReadingService = meterReadingService;
        this.costCalculationService = costCalculationService;
    }

    //TODO: could be renamed to getConsumptionCostForAllTariff
    public Optional<Map<String, BigDecimal>> getConsumptionCostOfElectricityReadingsForEachPricePlan(String smartMeterId) {
        Optional<List<ElectricityReading>> electricityReadings = meterReadingService.getReadings(smartMeterId);

        if (!electricityReadings.isPresent()) {
            return Optional.empty();
        }

        return Optional.of(powerSuppliers.stream().collect(
                Collectors.toMap(PowerSupplier::getPlanName, t -> costCalculationService.calculateCost(electricityReadings.get(), t))));
    }

}
