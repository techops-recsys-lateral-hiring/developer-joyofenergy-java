package uk.tw.energy.builders;

import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.MeterReadings;

import java.math.BigDecimal;
import java.time.Instant;
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

        Instant now = Instant.now();

        for (int i = 0; i < number; i++ ) {

            ElectricityReading reading = new ElectricityReading(now.minusSeconds(i * 3600), BigDecimal.ONE);
            this.addElectricityReading(reading);

        }

        return this;

    }

    public MeterReadingsBuilder addElectricityReading(ElectricityReading reading) {

        this.electricityReadings.add(reading);
        return this;

    }

    public MeterReadings build() {

        return new MeterReadings(meterId, electricityReadings);

    }

}
