package uk.tw.energy.dto;

import uk.tw.energy.domain.ElectricityReading;

import java.util.List;

public class MeterReadingsDto {

    private List<ElectricityReading> readings;

    public MeterReadingsDto() {}

    public MeterReadingsDto(List<ElectricityReading> readings) {
        this.readings = readings;
    }

    public List<ElectricityReading> getReadings() {
        return readings;
    }
}
