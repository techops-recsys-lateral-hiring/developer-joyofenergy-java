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

    @Test
    public void getConsumptionCalculatesDifferenceBetweenFirstAndLastReading() {
        Instant beginningOfDay = Instant.ofEpochMilli(1504134000);
        Instant beginningOfFollowingDay = Instant.ofEpochMilli(1504137600);
        ElectricityReading firstReading = new ElectricityReading(beginningOfDay, BigDecimal.valueOf(1000.000));
        ElectricityReading lastReading = new ElectricityReading(beginningOfFollowingDay, BigDecimal.valueOf(1012.000));
        List<ElectricityReading> electricityReadings = new ArrayList<>();
        electricityReadings.add(firstReading);
        electricityReadings.add(lastReading);
        MeterData meterData = new MeterData(electricityReadings);

        assertThat(meterData.getConsumption()).isEqualTo(new BigDecimal("12"));
    }
}
