package uk.tw.energy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.PricePlan;

@ExtendWith(MockitoExtension.class)
class PricePlanServiceTest {

	private static final PricePlan repsolPrice = new PricePlan("price-plan-0", "Repsol", BigDecimal.valueOf(1), new ArrayList<>());

	@Mock
	private List<PricePlan> pricePlans;

	@Mock
	private MeterReadingService meterReadingService;

	@InjectMocks
	PricePlanService pricePlanService;

	void testGetConsumptionCostOfElectricityReadingsForEachPricePlan() {
		fail("Not yet implemented");
	}

	public static List<ElectricityReading> generateReadingsForOneMonth() {
		List<ElectricityReading> readings = new ArrayList<>();
		Instant currentTime = Instant.now();

		for (int i = 1; i <= 30; i++) { // Assuming a month has 30 days
			Instant readingTime = currentTime.minus(i, ChronoUnit.DAYS);
			BigDecimal randomReading = BigDecimal.valueOf(1000); // Generate random reading
			readings.add(new ElectricityReading(readingTime, randomReading));
		}

		return readings;
	}

	@Test
	void testViewConsumptionCostOfLastWeek() {

		when(pricePlans.get(anyInt()))
				.thenReturn(repsolPrice);

		when(meterReadingService.getReadings(anyString())).thenReturn(Optional.of(generateReadingsForOneMonth()));

		BigDecimal ccoltw = pricePlanService.getConsumptionCostOfLastWeek("smartMeterId");

		assertTrue(ccoltw.compareTo(BigDecimal.ZERO) > 0);	
	}

	@Test
	void testViewConsumptionCostOfLastWeek_ReadingsNull() {

		when(meterReadingService.getReadings(anyString())).thenReturn(Optional.ofNullable(null));

		try {
			pricePlanService.getConsumptionCostOfLastWeek("anyString");
			fail("Expected RuntimeException was not thrown");
		} catch (ReadingException e) {
			assertTrue(e instanceof ReadingException);
			assertEquals("No readings found for smart meter id: anyString", e.getMessage());
		}
	}

	@Test
	void testViewConsumptionCostOfLastWeek_ReadingsWithin3weeks() {
		when(pricePlans.get(anyInt()))
				.thenReturn(repsolPrice);
		Optional<List<ElectricityReading>> oneMonthReadings = Optional.of(generateReadingsForOneMonth());
		when(meterReadingService.getReadings(anyString())).thenReturn(oneMonthReadings);

		BigDecimal ccoltw = pricePlanService.getConsumptionCostOfLastWeek("anyString");

		assertEquals(BigDecimal.valueOf(7), ccoltw);
	}

}
