package uk.tw.energy.service;

import org.junit.Test;
import uk.tw.energy.domain.MeterData;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class MeterReadingServiceTest {

    @Test
    public void givenMeterIdThatDoesNotExistShouldReturnNull() {

        MeterReadingService meterReadingService = new MeterReadingService();

        assertThat(meterReadingService.getReadings("unknown-id")).isEqualTo(Optional.empty());

    }

    @Test
    public void givenMeterReadingThatExistsShouldReturnMeterReadings() {

        MeterReadingService meterReadingService = new MeterReadingService();
        meterReadingService.storeReadings(new MeterData("random-id", new ArrayList<>()));

        assertThat(meterReadingService.getReadings("random-id")).isEqualTo(Optional.of(new ArrayList<>()));

    }

}
