/**
 * Provides a service for retrieving and storing meter readings.
 *
 * <p>This service provides the ability to retrieve the list of electricity readings associated with
 * a smart meter ID, and to store new readings for a smart meter.
 */
package uk.tw.energy.service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Service;
import uk.tw.energy.domain.ElectricityReading;

/**
 * Service for retrieving and storing meter readings.
 *
 * <p>This service provides the ability to retrieve the list of electricity readings associated with
 * a smart meter ID, and to store new readings for a smart meter.
 */
@Service
public class MeterReadingService {

  private final ConcurrentHashMap<String, List<ElectricityReading>> meterAssociatedReadings;

  /**
   * Creates a new instance of the meter reading service.
   *
   * @param meterAssociatedReadings the map of smart meter IDs to their associated electricity
   *     readings
   */
  public MeterReadingService(
      ConcurrentHashMap<String, List<ElectricityReading>> meterAssociatedReadings) {
    this.meterAssociatedReadings = meterAssociatedReadings;
  }

  /**
   * Retrieves the list of electricity readings associated with a smart meter ID.
   *
   * @param smartMeterId the ID of the smart meter to retrieve readings for
   * @return an optional containing the list of electricity readings, or empty if not found
   */
  public Optional<List<ElectricityReading>> getReadings(String smartMeterId) {
    return Optional.ofNullable(meterAssociatedReadings.get(smartMeterId));
  }

  /**
   * Stores a list of electricity readings for a given smart meter.
   *
   * @param smartMeterId the ID of the smart meter
   * @param electricityReadings the list of electricity readings to store
   */
  public void storeReadings(String smartMeterId, List<ElectricityReading> electricityReadings) {
    meterAssociatedReadings.compute(
        smartMeterId,
        (key, existingList) -> {
          if (existingList == null) {
            return new CopyOnWriteArrayList<>(electricityReadings);
          } else {
            existingList.addAll(electricityReadings);
            return existingList;
          }
        });
  }
}
