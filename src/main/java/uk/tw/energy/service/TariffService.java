package uk.tw.energy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.Tariff;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TariffService {
    @Autowired
    private final List<Tariff> tariffs;
    private MeterReadingService meterReadingService;

    public TariffService(List<Tariff> tariffs, MeterReadingService meterReadingService) {

        this.tariffs = tariffs;
        this.meterReadingService = meterReadingService;

    }

    public Map<String, BigDecimal> getConsumptionCostOfElectricityReadingsForEachTariff(String meterId) {

        List<ElectricityReading> electricityReadings = meterReadingService.getReadings(meterId).get();

        return tariffs.stream().collect(Collectors.toMap(Tariff::getSupplier, t -> calculateCost(electricityReadings, t)));


    }

    private BigDecimal calculateCost(List<ElectricityReading> electricityReadings, Tariff tariff) {

        BigDecimal average = calculateAverageReading(electricityReadings);
        BigDecimal timeElapsed = calculateTimeElapsed(electricityReadings);

        return average.divide(timeElapsed).multiply(tariff.getUnitRate());

    }

    private BigDecimal calculateAverageReading(List<ElectricityReading> electricityReadings) {

        BigDecimal summedReadings = electricityReadings.stream()
                .map(ElectricityReading::getReading)
                .reduce(BigDecimal.ZERO, (reading, accumulator) -> reading.add(accumulator));

        return summedReadings.divide(BigDecimal.valueOf(electricityReadings.size()));

    }

    private BigDecimal calculateTimeElapsed(List<ElectricityReading> electricityReadings) {

        ElectricityReading first = electricityReadings.stream().min(Comparator.comparing(ElectricityReading::getTime)).get();
        ElectricityReading last = electricityReadings.stream().max(Comparator.comparing(ElectricityReading::getTime)).get();

        return BigDecimal.valueOf(Duration.between(first.getTime(), last.getTime()).getSeconds() / 3600.0);

    }

}
