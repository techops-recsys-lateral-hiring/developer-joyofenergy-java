/**
 * The {@link ElectricityReading} class represents an electricity reading consisting of a timestamp
 * and a reading value in kilowatts (kW).
 */
package uk.tw.energy.domain;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Represents an electricity reading consisting of a timestamp and a reading value in kilowatts
 * (kW).
 *
 * @param time the timestamp of the reading
 * @param reading the reading value in kilowatts (kW)
 */
public record ElectricityReading(Instant time, BigDecimal reading) {
  /**
   * Create a new {@link ElectricityReading} with the given timestamp and reading value.
   *
   * @param time the timestamp of the reading
   * @param reading the reading value in kilowatts (kW)
   * @return the newly created {@link ElectricityReading}
   */
  public static ElectricityReading of(Instant time, BigDecimal reading) {
    return new ElectricityReading(time, reading);
  }
}
