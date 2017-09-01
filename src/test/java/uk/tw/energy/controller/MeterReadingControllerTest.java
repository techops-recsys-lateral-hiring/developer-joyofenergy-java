package uk.tw.energy.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
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
    private String meterId = "10101010";

    @Before
    public void setUp() {

        this.meterReadingService = new MeterReadingService();
        this.meterReadingController = new MeterReadingController(meterReadingService);

    }

    @Test
    public void givenEmptyMeterReadingShouldReturnOK() {

        MeterData meterData = new MeterData("bob", Collections.emptyList());
        assertThat(meterReadingController.storeReadings(meterData).getStatusCode()).isEqualTo(HttpStatus.OK);

    }

    @Test
    public void givenMultipleBatchesOfMeterReadingsShouldStore() {

        ElectricityReading firstReading = new ElectricityReading(Instant.now(), BigDecimal.ONE);
        ElectricityReading secondReading = new ElectricityReading(Instant.now(), BigDecimal.TEN);
        MeterData meterData = new MeterData(meterId, Arrays.asList(firstReading));
        MeterData otherMeterDataBatch = new MeterData(meterId, Arrays.asList(secondReading));

        meterReadingController.storeReadings(meterData);
        meterReadingController.storeReadings(otherMeterDataBatch);

        List<ElectricityReading> expectedElectricityReadings = Arrays.asList(firstReading, secondReading);
        assertThat(meterReadingService.getReadings(meterId).get()).isEqualTo(expectedElectricityReadings);

    }

    @Test
    public void givenMeterReadingsAssociatedWithTheUserShouldStoreAssociatedWithUser() {

        List<ElectricityReading> electricityReadingList = Arrays.asList(new ElectricityReading(Instant.now(), BigDecimal.ONE));
        MeterData meterData = new MeterData(meterId, electricityReadingList);
        meterReadingController.storeReadings(meterData);

        List<ElectricityReading> otherElectricityReadingList = Arrays.asList(new ElectricityReading(Instant.now(), BigDecimal.TEN));
        MeterData otherMeterData = new MeterData("rita", otherElectricityReadingList);
        meterReadingController.storeReadings(otherMeterData);

        assertThat(meterReadingService.getReadings(meterId).get()).isEqualTo(electricityReadingList);

    }

    @Test
    public void givenMeterIdShouldReturnAMeterReadingAssociatedWithMeterId() {

        List<ElectricityReading> otherElectricityReadingList = Arrays.asList(new ElectricityReading(Instant.now(), BigDecimal.TEN));
        MeterData otherMeterData = new MeterData(meterId, otherElectricityReadingList);

        meterReadingController.storeReadings(otherMeterData);

        assertThat(meterReadingController.readReadings(meterId).getBody()).isEqualTo(otherElectricityReadingList);

    }

    @Test
    public void givenMeterIdThatIsNotRecognisedShouldReturnNotFound() {

        assertThat(meterReadingController.readReadings(meterId).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    }

}
