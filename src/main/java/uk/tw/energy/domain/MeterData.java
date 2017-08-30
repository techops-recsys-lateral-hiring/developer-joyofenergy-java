package uk.tw.energy.domain;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

public class MeterData {
    private final List<ElectricityReading> electricityReadings;

    public MeterData(List<ElectricityReading> electricityReadings) {
        this.electricityReadings = electricityReadings;
    }

    public BigDecimal getConsumption() {
        ElectricityReading first = electricityReadings.stream().min(Comparator.comparing(ElectricityReading::getTime)).get();
        ElectricityReading last = electricityReadings.stream().max(Comparator.comparing(ElectricityReading::getTime)).get();
        return last.getReading().subtract(first.getReading());
    }
}
