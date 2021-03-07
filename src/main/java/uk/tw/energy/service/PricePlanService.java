package uk.tw.energy.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.PricePlan;

public interface PricePlanService {

	public Map<String, Object> calculateCostForEachPricePlan(String smartMeterId, String pricePlanId);

	public List<Map.Entry<String, BigDecimal>> recommendedCheapestPricePlan(String smartMeterId, Integer limit);

	default BigDecimal calculateCost(List<ElectricityReading> electricityReadings, PricePlan pricePlan) {
		BigDecimal average = calculateAverageReading(electricityReadings);
		BigDecimal timeElapsed = calculateTimeElapsed(electricityReadings);

		BigDecimal averagedCost = average.divide(timeElapsed, RoundingMode.HALF_UP);
		return averagedCost.multiply(pricePlan.getUnitRate());
	}

	default BigDecimal calculateAverageReading(List<ElectricityReading> electricityReadings) {
		BigDecimal summedReadings = electricityReadings.stream().map(ElectricityReading::getReading)
				.reduce(BigDecimal.ZERO, (reading, accumulator) -> reading.add(accumulator));

		return summedReadings.divide(BigDecimal.valueOf(electricityReadings.size()), RoundingMode.HALF_UP);
	}

	default BigDecimal calculateTimeElapsed(List<ElectricityReading> electricityReadings) {
		ElectricityReading first = electricityReadings.stream().min(Comparator.comparing(ElectricityReading::getTime))
				.get();
		ElectricityReading last = electricityReadings.stream().max(Comparator.comparing(ElectricityReading::getTime))
				.get();

		return BigDecimal.valueOf(Duration.between(first.getTime(), last.getTime()).getSeconds() / 3600.0);
	}

}
