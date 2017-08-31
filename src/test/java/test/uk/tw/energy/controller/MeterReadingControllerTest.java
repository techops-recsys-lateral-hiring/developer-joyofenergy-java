package test.uk.tw.energy.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import uk.tw.energy.controller.MeterReadingController;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.MeterData;
import uk.tw.energy.service.MeterReadingService;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class MeterReadingControllerTest {

    private MeterReadingController meterReadingController;
    private MeterReadingService meterReadingService;

    @Before
    public void setUp() {

        this.meterReadingService = new MeterReadingService();
        this.meterReadingController = new MeterReadingController(meterReadingService);

    }

    @Test
    public void givenEmptyMeterReadingShouldReturnOK() {

        MeterData meterData = new MeterData(Collections.emptyList());
        assertThat(meterReadingController.storeReading(meterData).getStatusCode()).isEqualTo(HttpStatus.OK);

    }

    @Test
    public void givenMeterReadingsShouldStore() {

        List<ElectricityReading> electricityReadingList = Arrays.asList(new ElectricityReading(Instant.now(), BigDecimal.ONE));
        MeterData meterData = new MeterData(electricityReadingList);
        meterReadingController.storeReading(meterData);
        assertThat(meterReadingService.getReadings()).isEqualTo(electricityReadingList);

    }

    @Test
    public void givenMultipleBatchesOfMeterReadingsShouldStore() {

        ElectricityReading firstReading = new ElectricityReading(Instant.now(), BigDecimal.ONE);
        ElectricityReading secondReading = new ElectricityReading(Instant.now(), BigDecimal.TEN);
        MeterData meterData = new MeterData(Arrays.asList(firstReading));
        MeterData otherMeterDataBatch = new MeterData(Arrays.asList(secondReading));

        meterReadingController.storeReading(meterData);
        meterReadingController.storeReading(otherMeterDataBatch);

        List<ElectricityReading> expectedElectricityReadings = Arrays.asList(firstReading, secondReading);
        assertThat(meterReadingService.getReadings()).isEqualTo(expectedElectricityReadings);

    }

}
