/**
 * Domain class for representing a collection of electricity readings for a smart meter.
 *
 * <p>This class is an immutable value object that represents a collection of electricity readings
 * for a given smart meter. It holds the unique identifier of the smart meter, and a list of {@link
 * ElectricityReading} objects.
 */
package uk.tw.energy.domain;

import java.util.List;

/**
 * Represents a collection of electricity readings for a given smart meter.
 *
 * <p>This class is an immutable value object that holds the identifier of a smart meter, and a list
 * of {@link ElectricityReading} objects.
 *
 * <p>The {@code smartMeterId} is a unique identifier for the smart meter, and the {@code
 * electricityReadings} is a list of {@link ElectricityReading} objects that represent the
 * electricity readings for the smart meter.
 *
 * @param smartMeterId the unique identifier of the smart meter
 * @param electricityReadings the list of electricity readings for the smart meter
 */
public record MeterReadings(String smartMeterId, List<ElectricityReading> electricityReadings) {}
