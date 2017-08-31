package test.uk.tw.energy.service;

import org.junit.Test;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.MeterData;
import uk.tw.energy.domain.Tariff;
import uk.tw.energy.service.CostService;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CostServiceTest {

    private CostService costService = new CostService();

    private ElectricityReading beginningOfDayReading =
            new ElectricityReading(Instant.ofEpochSecond(1504051200), BigDecimal.valueOf(1000.000));
    private ElectricityReading endOfDayReading =
            new ElectricityReading(Instant.ofEpochSecond(1504137600), BigDecimal.valueOf(1012.000));

    @Test
    public void getCostReturnsCostForMeterReadingOnTariff() {
        BigDecimal unitRate = BigDecimal.valueOf(0.20);
        Tariff tariff = new Tariff("Elmo's Excellent Electricity", unitRate, Collections.emptyList());

        List<ElectricityReading> electricityReadings = new ArrayList<>();
        electricityReadings.add(beginningOfDayReading);
        electricityReadings.add(endOfDayReading);
        MeterData meterData = new MeterData("rita", electricityReadings);

        BigDecimal cost = costService.calculateCost(meterData, tariff);

        assertThat(cost).isEqualTo(new BigDecimal("2.40"));
    }
}
