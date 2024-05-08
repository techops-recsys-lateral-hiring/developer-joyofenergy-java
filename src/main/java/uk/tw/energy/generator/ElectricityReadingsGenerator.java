/**
 * Generates a list of {@link ElectricityReading} objects.
 *
 * <p>Used to generate test data for the application.
 */
package uk.tw.energy.generator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import uk.tw.energy.domain.ElectricityReading;

/**
 * Generates a list of {@link ElectricityReading} objects.
 *
 * <p>Used to generate test data for the application.
 */
public class ElectricityReadingsGenerator {

  /** Constructs a new ElectricityReadingsGenerator. */
  public ElectricityReadingsGenerator() {}

  /**
   * Generates a list of ElectricityReading objects.
   *
   * @param number the number of ElectricityReading objects to generate
   * @return a list of ElectricityReading objects, sorted by time in ascending order
   */
  public List<ElectricityReading> generate(int number) {
    List<ElectricityReading> readings = new ArrayList<>();
    Instant now = Instant.now();

    Random readingRandomiser = new Random();
    for (int i = 0; i < number; i++) {
      double positiveRandomValue = Math.abs(readingRandomiser.nextGaussian());
      BigDecimal randomReading =
          BigDecimal.valueOf(positiveRandomValue).setScale(4, RoundingMode.CEILING);
      ElectricityReading electricityReading =
          new ElectricityReading(now.minusSeconds(i * 10L), randomReading);
      readings.add(electricityReading);
    }

    readings.sort(Comparator.comparing(ElectricityReading::time));
    return readings;
  }
}
