package uk.tw.energy.domain;

import java.util.List;

public class MeterReadings {

    private List<ElectricityReading> electricityReadings;
    private String meterId;

    public MeterReadings() { }

    public MeterReadings(String meterId, List<ElectricityReading> electricityReadings) {
        this.meterId = meterId;
        this.electricityReadings = electricityReadings;
    }

    public List<ElectricityReading> getElectricityReadings() {
        return electricityReadings;
    }

    public String getMeterId() {
        return meterId;
    }
}
