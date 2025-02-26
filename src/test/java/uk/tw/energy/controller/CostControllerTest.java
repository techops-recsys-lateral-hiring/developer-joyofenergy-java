package uk.tw.energy.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import uk.tw.energy.builders.MeterReadingsBuilder;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.MeterReadings;
import uk.tw.energy.service.MeterReadingService;

public class CostControllerTest {
    private static final String SMART_METER_ID = "10101010";

    private CostController costController;
    private MeterReadingService meterReadingService;

    @BeforeEach
    public void setUp() {
        this.meterReadingService = new MeterReadingService(new HashMap<>());
        this.costController = new CostController();
    }

    @Test
    public void givenMeterIdIsSuppliedWhenNoReadingAvailableShouldReturnZeroCost() {
        assertThat(costController.getLastWeekCost(SMART_METER_ID).getStatusCode())
                .isEqualTo(HttpStatus.OK);
        assertThat(costController.getLastWeekCost(SMART_METER_ID).getBody()).isEqualTo(BigDecimal.valueOf(0L));
    }

    @Test
    public void givenMeterIdIsSuppliedWhenReadingsAreAvailableShouldReturnWeeklyCost() {
      // MeterReadings meterReadings = new MeterReadingsBuilder()
      //           .setSmartMeterId(SMART_METER_ID)
      //           .generateElectricityReadings()
      //           .build();
        Instant now = Instant.now();

        ElectricityReading electricityReading1 = new ElectricityReading(now.minusSeconds(3600 * 48L), BigDecimal.valueOf(0.4802));
        ElectricityReading electricityReading2 = new ElectricityReading(now.minusSeconds(3600 * 24L), BigDecimal.valueOf(0.5002));
        List<ElectricityReading> meterReadings = List.of(electricityReading1, electricityReading2);

        meterReadingService.storeReadings(SMART_METER_ID, meterReadings);

        assertThat(costController.getLastWeekCost(SMART_METER_ID).getStatusCode())
                .isEqualTo(HttpStatus.OK);
        assertThat(costController.getLastWeekCost(SMART_METER_ID).getBody()).isEqualTo(BigDecimal.valueOf(0L));
    }
}
