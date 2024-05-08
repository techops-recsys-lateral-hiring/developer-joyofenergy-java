/** Builder for creating {@link MeterReadings} objects for testing purposes. */
package uk.tw.energy.builders;

import java.util.ArrayList;
import java.util.List;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.MeterReadings;
import uk.tw.energy.generator.ElectricityReadingsGenerator;

/**
 * A builder class for creating {@link MeterReadings} objects for testing purposes.
 *
 * <p>This class provides a fluent API for creating {@link MeterReadings} objects. The builder
 * allows the smart meter ID to be specified, and then allows electricity readings to be added to
 * the builder. Once the builder is complete, the {@link #build()} method can be called to generate
 * a {@link MeterReadings} object.
 */
public class MeterReadingsBuilder {

  private static final String DEFAULT_METER_ID = "id";
  private String smartMeterId = DEFAULT_METER_ID;
  private List<ElectricityReading> electricityReadings = new ArrayList<>();

  /**
   * Creates a new instance of the {@link MeterReadingsBuilder} class.
   *
   * <p>The default smart meter ID is set to "id" and an empty list of electricity readings is
   * initialised.
   */
  public MeterReadingsBuilder() {}

  /**
   * Sets the smart meter ID for the MeterReadingsBuilder.
   *
   * @param smartMeterId the smart meter ID to set
   * @return the updated MeterReadingsBuilder instance
   */
  public MeterReadingsBuilder setSmartMeterId(String smartMeterId) {
    this.smartMeterId = smartMeterId;
    return this;
  }

  /**
   * Generates electricity readings by calling the `generateElectricityReadings` method with a
   * default number of 5.
   *
   * @return the updated `MeterReadingsBuilder` instance with electricity readings generated
   */
  public MeterReadingsBuilder generateElectricityReadings() {
    return generateElectricityReadings(5);
  }

  /**
   * Generates a specified number of electricity readings and updates the electricityReadings field
   * of the MeterReadingsBuilder instance.
   *
   * @param number the number of electricity readings to generate
   * @return the updated MeterReadingsBuilder instance
   */
  public MeterReadingsBuilder generateElectricityReadings(int number) {
    ElectricityReadingsGenerator readingsBuilder = new ElectricityReadingsGenerator();
    this.electricityReadings = readingsBuilder.generate(number);
    return this;
  }

  /**
   * Builds and returns a MeterReadings instance with the specified smart meter ID and electricity
   * readings.
   *
   * @return the constructed MeterReadings instance
   */
  public MeterReadings build() {
    return new MeterReadings(smartMeterId, electricityReadings);
  }
}
