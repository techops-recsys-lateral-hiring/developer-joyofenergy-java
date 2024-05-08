/**
 * The {@code uk.tw.energy.domain.PricePlan} class represents a price plan offered by an energy
 * supplier. A price plan has a name, an energy supplier, a unit rate (i.e. the price per kWh), and
 * a list of peak time multipliers. The peak time multipliers are used to calculate the price for a
 * given {@link java.time.LocalDateTime}.
 */
package uk.tw.energy.domain;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a price plan offered by an energy supplier. A price plan has a name, an energy
 * supplier, a unit rate (i.e. the price per kWh), and a list of peak time multipliers. The peak
 * time multipliers are used to calculate the price for a given {@link LocalDateTime}.
 */
public class PricePlan {

  private final String energySupplier;
  private final String planName;
  private final BigDecimal unitRate; // unit price per kWh
  private final List<PeakTimeMultiplier> peakTimeMultipliers;

  /**
   * Creates a new PricePlan.
   *
   * @param planName the name of the price plan
   * @param energySupplier the name of the energy supplier
   * @param unitRate the unit rate of the price plan (i.e. the price per kWh)
   * @param peakTimeMultipliers a list of PeakTimeMultiplier objects representing the peak time
   *     multipliers for this plan (if any)
   */
  public PricePlan(
      String planName,
      String energySupplier,
      BigDecimal unitRate,
      List<PeakTimeMultiplier> peakTimeMultipliers) {
    this.planName = planName;
    this.energySupplier = energySupplier;
    this.unitRate = unitRate;
    this.peakTimeMultipliers = peakTimeMultipliers;
  }

  /**
   * Returns the energy supplier associated with this price plan.
   *
   * @return the name of the energy supplier
   */
  public String getEnergySupplier() {
    return energySupplier;
  }

  /**
   * Returns the name of the plan.
   *
   * @return the name of the plan
   */
  public String getPlanName() {
    return planName;
  }

  /**
   * Returns the unit rate of the object.
   *
   * @return the unit rate of the object
   */
  public BigDecimal getUnitRate() {
    return unitRate;
  }

  /**
   * Calculates and returns the price based on the given LocalDateTime.
   *
   * @param dateTime the LocalDateTime for which the price is calculated
   * @return the calculated price based on the peak time multipliers and unit rate
   */
  public BigDecimal getPrice(LocalDateTime dateTime) {
    return peakTimeMultipliers.stream()
        .filter(multiplier -> multiplier.dayOfWeek.equals(dateTime.getDayOfWeek()))
        .findFirst()
        .map(multiplier -> unitRate.multiply(multiplier.multiplier))
        .orElse(unitRate);
  }

  static class PeakTimeMultiplier {

    DayOfWeek dayOfWeek;
    BigDecimal multiplier;

    public PeakTimeMultiplier(DayOfWeek dayOfWeek, BigDecimal multiplier) {
      this.dayOfWeek = dayOfWeek;
      this.multiplier = multiplier;
    }
  }
}
