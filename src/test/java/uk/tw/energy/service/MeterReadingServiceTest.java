package uk.tw.energy.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uk.tw.energy.domain.MeterReadings;
import uk.tw.energy.exception.ObjectNotFoundException;

public class MeterReadingServiceTest {

	private MeterReadingService meterReadingService;

	@BeforeEach
	public void setUp() {
		meterReadingService = new MeterReadingServiceImpl(new HashMap<>());
	}

	@Test
	public void givenMeterIdThatDoesNotExistShouldReturnNull() {
		assertThrows(ObjectNotFoundException.class, () -> meterReadingService.getReadings("unknown-id"));
	}

	@Test
	public void givenMeterReadingThatExistsShouldReturnMeterReadings() {
		meterReadingService.storeReadings(new MeterReadings("random-id", new ArrayList<>()));
		assertThat(meterReadingService.getReadings("random-id")).isEqualTo(new ArrayList<>());
	}
}
