package uk.tw.energy.domain;

import java.math.BigDecimal;
import java.util.List;

public class MeterData {
    private final List<ElectricityReading> electricityReadings;

    public MeterData(List<ElectricityReading> electricityReadings) {
        this.electricityReadings = electricityReadings;
    }

    public BigDecimal getConsumption() {
        ElectricityReading first = electricityReadings.get(0);
        ElectricityReading last = electricityReadings.get(electricityReadings.size() - 1);
        return last.getReading().subtract(first.getReading());
    }
}
