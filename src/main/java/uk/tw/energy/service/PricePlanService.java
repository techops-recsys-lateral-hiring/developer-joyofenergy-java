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
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.PeakTimeMultiplier;
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

    LocalDateTime calculationTime = LocalDateTime.now();
    return Optional.of(
        pricePlans.stream()
            .collect(
                Collectors.toMap(
                    PricePlan::getPlanName,
                    pricePlan ->
                        calculateCost(electricityReadings.get(), pricePlan, calculationTime))));
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
      List<ElectricityReading> electricityReadings,
      PricePlan pricePlan,
      LocalDateTime calculationTime) {
    BigDecimal average = calculateAverageReading(electricityReadings);
    BigDecimal timeElapsed = calculateTimeElapsed(electricityReadings);

    if (timeElapsed.compareTo(BigDecimal.ZERO) == 0) {
      return BigDecimal.ZERO;
    }

    BigDecimal averagedCost = average.divide(timeElapsed, RoundingMode.HALF_UP);
    BigDecimal unitCost = pricePlan.getPrice(calculationTime);
    return averagedCost.multiply(unitCost);
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
            .reduce(BigDecimal.ZERO, BigDecimal::add);

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
        electricityReadings.stream()
            .min(Comparator.comparing(ElectricityReading::time))
            .orElseThrow(() -> new IllegalArgumentException("No readings available"));

    ElectricityReading last =
        electricityReadings.stream()
            .max(Comparator.comparing(ElectricityReading::time))
            .orElseThrow(() -> new IllegalArgumentException("No readings available"));

    return BigDecimal.valueOf(Duration.between(first.time(), last.time()).toSeconds() / 3600.0);
  }

  /**
   * Retrieves the price plan with the specified plan name.
   *
   * @param planName the name of the plan to retrieve
   * @return an Optional containing the price plan with the specified name, or an empty Optional if
   *     no such plan exists
   */
  public Optional<PricePlan> getPricePlanByName(String planName) {
    return pricePlans.stream().filter(plan -> plan.getPlanName().equals(planName)).findFirst();
  }

  /**
   * Retrieves all price plans.
   *
   * @return a list of PricePlan objects representing all price plans
   */
  public List<PricePlan> getAllPricePlans() {
    return pricePlans;
  }

  /**
   * Adds a peak time multiplier to the specified price plan if the plan exists.
   *
   * @param planName the name of the plan to add the multiplier to
   * @param multiplier the peak time multiplier to add
   */
  public void addPeakTimeMultiplierToPlan(String planName, PeakTimeMultiplier multiplier) {
    Optional<PricePlan> optionalPricePlan = getPricePlanByName(planName);
    optionalPricePlan.ifPresent(
        pricePlan -> {
          pricePlan.setPeakTimeMultiplier(multiplier);
          pricePlans.replaceAll(p -> p.getPlanName().equals(planName) ? pricePlan : p);
        });
  }
}
