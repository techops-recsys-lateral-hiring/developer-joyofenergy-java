/**
 * Unit tests for the {@link MeterReadingService} class.
 *
 * <p>The {@link MeterReadingService} class is responsible for managing meter readings.
 */
package uk.tw.energy.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link MeterReadingService} class.
 *
 * <p>The {@link MeterReadingService} class is responsible for managing meter readings.
 */
public class MeterReadingServiceTest {

  private MeterReadingService meterReadingService;

  /** Test class for {@link MeterReadingService}. */
  public MeterReadingServiceTest() {}

  /**
   * Sets up the test environment before each test case.
   *
   * <p>This method initializes the meterReadingService object with a new instance of
   * MeterReadingService using an empty ConcurrentHashMap.
   */
  @BeforeEach
  public void setUp() {
    meterReadingService = new MeterReadingService(new ConcurrentHashMap<>());
  }

  /**
   * Test case to verify that when a meter ID that does not exist is passed to the getReadings()
   * method of the MeterReadingService class, it returns an empty Optional.
   */
  @Test
  public void givenMeterIdThatDoesNotExistShouldReturnNull() {
    assertThat(meterReadingService.getReadings("unknown-id")).isEqualTo(Optional.empty());
  }

  /**
   * A test case to verify that when a meter ID exists, it should return the meter readings
   * correctly.
   */
  @Test
  public void givenMeterReadingThatExistsShouldReturnMeterReadings() {
    meterReadingService.storeReadings("random-id", new ArrayList<>());
    assertThat(meterReadingService.getReadings("random-id"))
        .isEqualTo(Optional.of(new ArrayList<>()));
  }
}
