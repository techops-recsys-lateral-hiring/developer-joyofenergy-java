package uk.tw.energy.service;

import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.MeterData;

import java.util.ArrayList;
import java.util.List;

public class MeterReadingService {

    private List<ElectricityReading> electricityReadings = new ArrayList<>();

    public List<ElectricityReading> getReadings() {

        return electricityReadings;

    }

    public void storeReadings(MeterData meterData) {

        this.electricityReadings.addAll(meterData.getElectricityReadings());

    }

}
