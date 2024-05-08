/**
 * This class represents the controller for handling meter readings. It provides endpoints for
 * getting and posting meter readings.
 */
package uk.tw.energy.controller;

import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.MeterReadings;
import uk.tw.energy.service.MeterReadingService;

/** This class represents the controller for handling meter readings. */
@RestController
@RequestMapping("/readings")
public class MeterReadingController {

  /** The service for handling meter readings. */
  private final MeterReadingService meterReadingService;

  /**
   * Constructor for MeterReadingController.
   *
   * @param meterReadingService The service for handling meter readings
   */
  public MeterReadingController(MeterReadingService meterReadingService) {
    this.meterReadingService = meterReadingService;
  }

  /**
   * Endpoint for storing meter readings.
   *
   * @param meterReadings The meter readings to store
   * @return ResponseEntity indicating the status of the operation
   */
  @PostMapping("/store")
  public ResponseEntity<?> storeReadings(@RequestBody MeterReadings meterReadings) {
    if (!isMeterReadingsValid(meterReadings)) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
    meterReadingService.storeReadings(
        meterReadings.smartMeterId(), meterReadings.electricityReadings());
    return ResponseEntity.ok().build();
  }

  /**
   * Method to validate if the meter readings are valid.
   *
   * @param meterReadings The meter readings to validate
   * @return true if the meter readings are valid, false otherwise
   */
  private boolean isMeterReadingsValid(MeterReadings meterReadings) {
    String smartMeterId = meterReadings.smartMeterId();
    List<ElectricityReading> electricityReadings = meterReadings.electricityReadings();
    return smartMeterId != null
        && !smartMeterId.isEmpty()
        && electricityReadings != null
        && !electricityReadings.isEmpty();
  }

  /**
   * Retrieves the readings for a specific smart meter ID.
   *
   * @param smartMeterId The ID of the smart meter
   * @return ResponseEntity containing the readings if found, or not found status
   */
  @GetMapping("/read/{smartMeterId}")
  public ResponseEntity<?> readReadings(@PathVariable String smartMeterId) {
    Optional<List<ElectricityReading>> readings = meterReadingService.getReadings(smartMeterId);
    return readings.isPresent()
        ? ResponseEntity.ok(readings.get())
        : ResponseEntity.notFound().build();
  }
}
