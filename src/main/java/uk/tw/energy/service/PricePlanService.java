package uk.tw.energy.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.PricePlan;

@Service
public class PricePlanService {
	
	private static WeekFields weekFields = WeekFields.of(Locale.getDefault());

	@Autowired
	private List<PricePlan> pricePlans;
	
	@Autowired
	private MeterReadingService meterReadingService;

	public PricePlanService(List<PricePlan> pricePlans, MeterReadingService meterReadingService) {
		this.pricePlans = pricePlans;
		this.meterReadingService = meterReadingService;
	}

	public Optional<Map<String, BigDecimal>> getConsumptionCostOfElectricityReadingsForEachPricePlan(
			String smartMeterId) {
		Optional<List<ElectricityReading>> electricityReadings = meterReadingService.getReadings(smartMeterId);

		if (!electricityReadings.isPresent()) {
			return Optional.empty();
		}

		return Optional.of(pricePlans.stream()
				.collect(Collectors.toMap(PricePlan::getPlanName, t -> calculateCost(electricityReadings.get(), t))));
	}

	private BigDecimal calculateCost(List<ElectricityReading> electricityReadings, PricePlan pricePlan) {
		BigDecimal averageReading = calculateAverageReading(electricityReadings);
		BigDecimal timeElapsed = calculateTimeElapsed(electricityReadings);
		System.out.println("average: " + averageReading + " timeElapsed: " + timeElapsed);
		//FIXME if you calculate the average cost you have to multiply by the timeElapsed
		BigDecimal averagedCost = averageReading.divide(timeElapsed, RoundingMode.HALF_UP);
		System.out.println("averaged reading/hr: " + averagedCost);
		return averagedCost.multiply(pricePlan.getUnitRate());
	}

	private BigDecimal calculateAverageReading(List<ElectricityReading> electricityReadings) {
		BigDecimal summedReadings = electricityReadings.stream().map(ElectricityReading::reading)
				.reduce(BigDecimal.ZERO, (reading, accumulator) -> reading.add(accumulator));

		return electricityReadings.size() == 0 ? BigDecimal.ZERO : summedReadings.divide(BigDecimal.valueOf(electricityReadings.size()), RoundingMode.HALF_UP);

	}
	/**
	 * Hours between first and last reading
	 * @param electricityReadings
	 * @return
	 */
	private BigDecimal calculateTimeElapsed(List<ElectricityReading> electricityReadings) {
		ElectricityReading first = electricityReadings.stream().min(Comparator.comparing(ElectricityReading::time))
				.get();

		ElectricityReading last = electricityReadings.stream().max(Comparator.comparing(ElectricityReading::time))
				.get();

		return BigDecimal.valueOf(Duration.between(first.time(), last.time()).getSeconds() / 3600.0);
	}

	public BigDecimal getConsumptionCostOfLastWeek(String metterId) {
		BigDecimal result = BigDecimal.ZERO;
		
		Optional<List<ElectricityReading>> electricityReadings = meterReadingService.getReadings(metterId);

		if (electricityReadings.isPresent()) {
			this.dump(electricityReadings.get());
			int lastWeekNumber = calcLastWeek();
            List<ElectricityReading> filtered = electricityReadings.get().stream()
                    .filter(reading -> this.getWeekNumber(reading.time()) == lastWeekNumber)
                    .collect(Collectors.toList());
            this.dump(filtered);
			result = this.calculateCost(filtered, pricePlans.get(0));
		} else {
			throw new ReadingException("No readings found for smart meter id: " + metterId);
		}
		// FIXME here we have to multiply the result with the unit rate
		return result ;
	}
	
	private int calcLastWeek() {
		ZonedDateTime lastWeekStart = ZonedDateTime.now().minusWeeks(1);
		int lastWeekNumber = getWeekNumber(lastWeekStart.toInstant());
		return lastWeekNumber;
	}

	private void dump(List<ElectricityReading> filtered) {
		System.out.println("Electricity Reading:");
		
		filtered.forEach(reading -> {
			int weekNumber = getWeekNumber(reading);
            System.out.println("Week " + weekNumber + ": " + reading.time() + " " + reading.reading() + "kw");

		});
	}

	private int getWeekNumber(ElectricityReading reading) {
		Instant instant = reading.time();
		int weekNumber = getWeekNumber(instant);
		return weekNumber;
	}

	private int getWeekNumber(Instant instant) {
		LocalDate date = instant.atZone(ZoneId.systemDefault()).toLocalDate();
		int weekNumber = date.get(weekFields.weekOfWeekBasedYear());
		return weekNumber;
	}
}
