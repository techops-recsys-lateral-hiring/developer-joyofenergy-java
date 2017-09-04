package uk.tw.energy.builders;

import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.MeterReadings;
import uk.tw.energy.generator.ElectricityReadingsGenerator;

import java.util.ArrayList;
import java.util.List;

public class MeterReadingsBuilder {

    private String meterId = "id";
    private List<ElectricityReading> electricityReadings = new ArrayList<>();

    public MeterReadingsBuilder setMeterId(String meterId) {

        this.meterId = meterId;
        return this;

    }

    public MeterReadingsBuilder generateElectricityReadings() {

        return generateElectricityReadings(5);

    }

    public MeterReadingsBuilder generateElectricityReadings(int number) {

        ElectricityReadingsGenerator readingsBuilder = new ElectricityReadingsGenerator();
        this.electricityReadings = readingsBuilder.generate(number);

        return this;

    }

    public MeterReadings build() {

        return new MeterReadings(meterId, electricityReadings);

    }

}
