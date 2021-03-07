package uk.tw.energy.domain;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PricePlan {

	private final String energySupplier;
	private final String planName;
	private final BigDecimal unitRate; // unit price per kWh
	private final List<PeakTimeMultiplier> peakTimeMultipliers;

	public BigDecimal getPrice(LocalDateTime dateTime) {
		return peakTimeMultipliers.stream().filter(multiplier -> multiplier.dayOfWeek.equals(dateTime.getDayOfWeek()))
				.findFirst().map(multiplier -> unitRate.multiply(multiplier.multiplier)).orElse(unitRate);
	}

	@AllArgsConstructor
	static class PeakTimeMultiplier {

		private DayOfWeek dayOfWeek;
		private BigDecimal multiplier;
	}
}
