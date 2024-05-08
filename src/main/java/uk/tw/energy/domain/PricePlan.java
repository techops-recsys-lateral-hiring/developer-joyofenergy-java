package uk.tw.energy.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A price plan for electricity consumption.
 *
 * <p>Represents a price plan for electricity consumption, including the base price, peak time
 * multiplier and any exceptional prices.
 */
public class PricePlan {

  private final String energySupplier;
  private final String planName;
  private final BigDecimal unitRate; // unit price per kWh
  private List<PeakTimeMultiplier> peakTimeMultipliers;

  /**
   * Creates a new {@link PricePlan} object.
   *
   * @param planName the name of the price plan
   * @param energySupplier the name of the energy supplier
   * @param unitRate the unit rate for this price plan
   * @param peakTimeMultipliers the list of peak time multipliers
   */
  public PricePlan(
      String planName,
      String energySupplier,
      BigDecimal unitRate,
      List<PeakTimeMultiplier> peakTimeMultipliers) {
    this.planName = planName;
    this.energySupplier = energySupplier;
    this.unitRate = unitRate;
    this.peakTimeMultipliers =
        peakTimeMultipliers != null
            ? new CopyOnWriteArrayList<>(peakTimeMultipliers)
            : new CopyOnWriteArrayList<>();
  }

  /**
   * Retrieves the name of the energy supplier.
   *
   * @return the name of the energy supplier
   */
  public String getEnergySupplier() {
    return energySupplier;
  }

  /**
   * Retrieves the name of the plan.
   *
   * @return the name of the plan
   */
  public String getPlanName() {
    return planName;
  }

  /**
   * Retrieves the unit rate associated with this price plan.
   *
   * @return the unit rate per kilowatt-hour
   */
  public BigDecimal getUnitRate() {
    return unitRate;
  }

  /**
   * A method to calculate the price based on the given date and time.
   *
   * @param dateTime the date and time to calculate the price for
   * @return the calculated price based on the date and time
   */
  public BigDecimal getPrice(LocalDateTime dateTime) {
    if (peakTimeMultipliers == null || peakTimeMultipliers.isEmpty()) {
      return unitRate; // Retorna a tarifa padrão se não houver multiplicadores.
    }
    return peakTimeMultipliers.stream()
        .filter(multiplier -> multiplier.isActiveDuring(dateTime))
        .findFirst()
        .map(PeakTimeMultiplier::getMultiplier)
        .map(multiplier -> unitRate.multiply(multiplier))
        .orElse(unitRate);
  }

  /**
   * Retrieves the list of peak time multipliers.
   *
   * @return the list of peak time multipliers
   */
  public List<PeakTimeMultiplier> getPeakTimeMultipliers() {
    return peakTimeMultipliers;
  }

  /**
   * Sets a peak time multiplier for the current object. If the multiplier already exists, it is
   * removed and replaced with the new one.
   *
   * @param multiplier the peak time multiplier to set
   */
  public void setPeakTimeMultiplier(PeakTimeMultiplier multiplier) {
    synchronized (this) {
      peakTimeMultipliers.removeIf(x -> x.equals(multiplier));
      peakTimeMultipliers.add(multiplier);
    }
  }
}
