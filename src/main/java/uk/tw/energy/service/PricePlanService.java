/**
 * Service for providing information about price plans.
 *
 * <p>Provides information about the available price plans and the cost of electricity consumption
 * based on the price plans and the readings of a smart meter.
 */
package uk.tw.energy.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.PricePlan;

/**
 * Service for providing information about price plans.
 *
 * <p>Provides information about the available price plans and the cost of electricity consumption
 * based on the price plans and the readings of a smart meter.
 */
@Service
public class PricePlanService {

  private final List<PricePlan> pricePlans;
  private final MeterReadingService meterReadingService;

  /**
   * Constructs a new {@code PricePlanService}.
   *
   * @param pricePlans the list of available price plans
   * @param meterReadingService the service for providing electricity readings
   */
  public PricePlanService(List<PricePlan> pricePlans, MeterReadingService meterReadingService) {
    this.pricePlans = pricePlans;
    this.meterReadingService = meterReadingService;
  }

  /**
   * Calculates the consumption cost of electricity readings for each price plan.
   *
   * @param smartMeterId the ID of the smart meter
   * @return a map containing the consumption cost for each price plan
   */
  public Optional<Map<String, BigDecimal>> getConsumptionCostOfElectricityReadingsForEachPricePlan(
      String smartMeterId) {
    Optional<List<ElectricityReading>> electricityReadings =
        meterReadingService.getReadings(smartMeterId);

    if (!electricityReadings.isPresent()) {
      return Optional.empty();
    }

    return Optional.of(
        pricePlans.stream()
            .collect(
                Collectors.toMap(
                    PricePlan::getPlanName, t -> calculateCost(electricityReadings.get(), t))));
  }

  /**
   * Calculates the cost of electricity readings based on the average reading and time elapsed,
   * multiplied by the unit rate of the given price plan.
   *
   * @param electricityReadings a list of electricity readings
   * @param pricePlan the price plan to calculate the cost for
   * @return the calculated cost of electricity readings
   */
  private BigDecimal calculateCost(
      List<ElectricityReading> electricityReadings, PricePlan pricePlan) {
    BigDecimal average = calculateAverageReading(electricityReadings);
    BigDecimal timeElapsed = calculateTimeElapsed(electricityReadings);

    BigDecimal averagedCost = average.divide(timeElapsed, RoundingMode.HALF_UP);
    return averagedCost.multiply(pricePlan.getUnitRate());
  }

  /**
   * Calculates the average reading from a list of ElectricityReadings.
   *
   * @param electricityReadings the list of ElectricityReadings to calculate the average from
   * @return the average reading as a BigDecimal
   */
  private BigDecimal calculateAverageReading(List<ElectricityReading> electricityReadings) {
    BigDecimal summedReadings =
        electricityReadings.stream()
            .map(ElectricityReading::reading)
            .reduce(BigDecimal.ZERO, (reading, accumulator) -> reading.add(accumulator));

    return summedReadings.divide(
        BigDecimal.valueOf(electricityReadings.size()), RoundingMode.HALF_UP);
  }

  /**
   * Calculates the time elapsed between the first and last electricity readings.
   *
   * @param electricityReadings a list of electricity readings
   * @return the time elapsed between the first and last reading in hours as a BigDecimal
   */
  private BigDecimal calculateTimeElapsed(List<ElectricityReading> electricityReadings) {
    ElectricityReading first =
        electricityReadings.stream().min(Comparator.comparing(ElectricityReading::time)).get();

    ElectricityReading last =
        electricityReadings.stream().max(Comparator.comparing(ElectricityReading::time)).get();

    return BigDecimal.valueOf(Duration.between(first.time(), last.time()).getSeconds() / 3600.0);
  }
}
