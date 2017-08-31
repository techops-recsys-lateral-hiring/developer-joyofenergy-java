package test.uk.tw.energy.domain;

import org.junit.Test;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.MeterData;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MeterDataTest {

    private ElectricityReading beginningOfDayReading =
            new ElectricityReading(Instant.ofEpochSecond(1504051200), BigDecimal.valueOf(1000.000));
    private ElectricityReading middleOfDayReading =
            new ElectricityReading(Instant.ofEpochSecond(1504094400), BigDecimal.valueOf(1006.000));
    private ElectricityReading endOfDayReading =
            new ElectricityReading(Instant.ofEpochSecond(1504137600), BigDecimal.valueOf(1012.000));

    @Test
    public void getConsumptionCalculatesConsumptionBetweenTwoReadings() {
        List<ElectricityReading> electricityReadings = new ArrayList<>();
        electricityReadings.add(beginningOfDayReading);
        electricityReadings.add(endOfDayReading);

        MeterData meterData = new MeterData("rita", electricityReadings);

        assertThat(meterData.getConsumption()).isEqualByComparingTo(BigDecimal.valueOf(12));
    }

    @Test
    public void getConsumptionCalculatesConsumptionForOutOfOrderReadings() {
        List<ElectricityReading> electricityReadings = new ArrayList<>();
        electricityReadings.add(beginningOfDayReading);
        electricityReadings.add(endOfDayReading);
        electricityReadings.add(middleOfDayReading);
        MeterData meterData = new MeterData("sue", electricityReadings);

        assertThat(meterData.getConsumption()).isEqualByComparingTo(BigDecimal.valueOf(12));
    }
}
