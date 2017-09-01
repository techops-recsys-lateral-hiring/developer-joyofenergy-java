package uk.tw.energy.service;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class MeterReadingServiceTest {

    private MeterReadingService meterReadingService = new MeterReadingService();

    @Test
    public void givenMeterIdThatDoesNotExistShouldReturnNull() {

        assertThat(meterReadingService.getReadings("unknown-id")).isEqualTo(Optional.empty());

    }

    @Test
    public void givenMeterReadingThatExistsShouldReturnMeterReadings() {

        meterReadingService.storeReadings("random-id", new ArrayList<>());

        assertThat(meterReadingService.getReadings("random-id")).isEqualTo(Optional.of(new ArrayList<>()));

    }

}
