package uk.tw.energy.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

public class MeterData {

    private List<ElectricityReading> electricityReadings;
    private String userId;

    public MeterData() { }

    public MeterData(String userId, List<ElectricityReading> electricityReadings) {

        this.userId = userId;
        this.electricityReadings = electricityReadings;

    }

    @JsonIgnore
    public BigDecimal getConsumption() {
        ElectricityReading first = electricityReadings.stream().min(Comparator.comparing(ElectricityReading::getTime)).get();
        ElectricityReading last = electricityReadings.stream().max(Comparator.comparing(ElectricityReading::getTime)).get();
        return last.getReading().subtract(first.getReading());
    }

    public List<ElectricityReading> getElectricityReadings() {
        return electricityReadings;
    }

    public String getUserId() {
        return userId;
    }
}
