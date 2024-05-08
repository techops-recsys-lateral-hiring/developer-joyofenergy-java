/**
 * Tests the {@link MeterReadingController} class. The {@link MeterReadingController} class is
 * responsible for handling incoming meter readings and storing them in the database.
 */
package uk.tw.energy.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import uk.tw.energy.builders.MeterReadingsBuilder;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.MeterReadings;
import uk.tw.energy.service.MeterReadingService;

/**
 * Tests the {@link MeterReadingController} class.
 *
 * <p>This class tests the methods of the {@link MeterReadingController} class. The {@link
 * MeterReadingController} class is responsible for handling incoming meter readings and storing
 * them in the database.
 */
public class MeterReadingControllerTest {

  private static final String SMART_METER_ID = "10101010";
  private MeterReadingController meterReadingController;
  private MeterReadingService meterReadingService;

  /** Constructor for {@link MeterReadingControllerTest}. */
  public MeterReadingControllerTest() {}

  /**
   * Sets up the test environment before each test case.
   *
   * <p>This method initializes the meterReadingService and meterReadingController objects with new
   * instances using an empty ConcurrentHashMap.
   */
  @BeforeEach
  public void setUp() {
    this.meterReadingService = new MeterReadingService(new ConcurrentHashMap<>());
    this.meterReadingController = new MeterReadingController(meterReadingService);
  }

  /** Test that an error response is returned when no meter ID is supplied for storing. */
  @Test
  public void givenNoMeterIdIsSuppliedWhenStoringShouldReturnErrorResponse() {
    MeterReadings meterReadings = new MeterReadings(null, Collections.emptyList());
    assertThat(meterReadingController.storeReadings(meterReadings).getStatusCode())
        .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  /** Test that an error response is returned when an empty meter reading is provided. */
  @Test
  public void givenEmptyMeterReadingShouldReturnErrorResponse() {
    MeterReadings meterReadings = new MeterReadings(SMART_METER_ID, Collections.emptyList());
    assertThat(meterReadingController.storeReadings(meterReadings).getStatusCode())
        .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  /**
   * Test case to verify that when null readings are supplied when storing meter readings, the
   * controller returns an error response with the HTTP status code INTERNAL_SERVER_ERROR.
   */
  @Test
  public void givenNullReadingsAreSuppliedWhenStoringShouldReturnErrorResponse() {
    MeterReadings meterReadings = new MeterReadings(SMART_METER_ID, null);
    assertThat(meterReadingController.storeReadings(meterReadings).getStatusCode())
        .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  /** Test case to verify that multiple batches of meter readings are stored correctly. */
  @Test
  public void givenMultipleBatchesOfMeterReadingsShouldStore() {
    MeterReadings meterReadings =
        new MeterReadingsBuilder()
            .setSmartMeterId(SMART_METER_ID)
            .generateElectricityReadings()
            .build();

    MeterReadings otherMeterReadings =
        new MeterReadingsBuilder()
            .setSmartMeterId(SMART_METER_ID)
            .generateElectricityReadings()
            .build();

    meterReadingController.storeReadings(meterReadings);
    meterReadingController.storeReadings(otherMeterReadings);

    List<ElectricityReading> expectedElectricityReadings = new ArrayList<>();
    expectedElectricityReadings.addAll(meterReadings.electricityReadings());
    expectedElectricityReadings.addAll(otherMeterReadings.electricityReadings());

    assertThat(meterReadingService.getReadings(SMART_METER_ID).get())
        .isEqualTo(expectedElectricityReadings);
  }

  /**
   * Test case to verify that meter readings associated with a user are stored correctly.
   *
   * <p>This test case creates two instances of MeterReadings, each with a different smart meter ID.
   * It then stores the readings for both meters using the meterReadingController. Finally, it
   * asserts that the readings for the first meter can be retrieved using the meterReadingService
   * and that they match the original readings.
   */
  @Test
  public void givenMeterReadingsAssociatedWithTheUserShouldStoreAssociatedWithUser() {
    MeterReadings meterReadings =
        new MeterReadingsBuilder()
            .setSmartMeterId(SMART_METER_ID)
            .generateElectricityReadings()
            .build();

    MeterReadings otherMeterReadings =
        new MeterReadingsBuilder().setSmartMeterId("00001").generateElectricityReadings().build();

    meterReadingController.storeReadings(meterReadings);
    meterReadingController.storeReadings(otherMeterReadings);

    assertThat(meterReadingService.getReadings(SMART_METER_ID).get())
        .isEqualTo(meterReadings.electricityReadings());
  }

  /**
   * Test case to verify that when a meter ID that is not recognized is provided, the controller
   * returns a NOT_FOUND status code.
   */
  @Test
  public void givenMeterIdThatIsNotRecognisedShouldReturnNotFound() {
    assertThat(meterReadingController.readReadings(SMART_METER_ID).getStatusCode())
        .isEqualTo(HttpStatus.NOT_FOUND);
  }
}
