/**
 * Unit tests for the {@link PricePlanComparatorController} class.
 *
 * <p>The {@link PricePlanComparatorController} class is responsible for comparing the price plans
 * and returning the best one.
 */
package uk.tw.energy.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.PricePlan;
import uk.tw.energy.service.AccountService;
import uk.tw.energy.service.MeterReadingService;
import uk.tw.energy.service.PricePlanService;

/**
 * Unit tests for the {@link PricePlanComparatorController} class which compares the price plans and
 * returns the best one.
 */
public class PricePlanComparatorControllerTest {

  private static final String PRICE_PLAN_1_ID = "test-supplier";
  private static final String PRICE_PLAN_2_ID = "best-supplier";
  private static final String PRICE_PLAN_3_ID = "second-best-supplier";
  private static final String SMART_METER_ID = "smart-meter-id";
  private PricePlanComparatorController controller;
  private MeterReadingService meterReadingService;
  private AccountService accountService;

  /** Default constructor for the test class. */
  public PricePlanComparatorControllerTest() {}

  /**
   * Sets up the test environment before each test case.
   *
   * <p>This method initializes the necessary objects for testing the {@link
   * PricePlanComparatorController} class. It creates a new instance of {@link MeterReadingService}
   * with an empty {@link ConcurrentHashMap} for meter readings. It creates three instances of
   * {@link PricePlan} with different IDs, prices, and other properties. It creates a list of price
   * plans and passes it along with the meter reading service to create a new instance of {@link
   * PricePlanService}. It creates a map of meters to tariffs and passes it to create a new instance
   * of {@link AccountService}. Finally, it creates a new instance of {@link
   * PricePlanComparatorController} with the price plan service and account service.
   */
  @BeforeEach
  public void setUp() {
    meterReadingService = new MeterReadingService(new ConcurrentHashMap<>());
    PricePlan pricePlan1 = new PricePlan(PRICE_PLAN_1_ID, null, BigDecimal.TEN, null);
    PricePlan pricePlan2 = new PricePlan(PRICE_PLAN_2_ID, null, BigDecimal.ONE, null);
    PricePlan pricePlan3 = new PricePlan(PRICE_PLAN_3_ID, null, BigDecimal.valueOf(2), null);

    List<PricePlan> pricePlans = Arrays.asList(pricePlan1, pricePlan2, pricePlan3);
    PricePlanService tariffService = new PricePlanService(pricePlans, meterReadingService);

    Map<String, String> meterToTariffs = new ConcurrentHashMap<>();
    meterToTariffs.put(SMART_METER_ID, PRICE_PLAN_1_ID);
    accountService = new AccountService(meterToTariffs);

    controller = new PricePlanComparatorController(tariffService, accountService);
  }

  /**
   * Test case to verify that the calculated cost for meter readings for every price plan is
   * correct.
   *
   * <p>This test case creates two electricity readings, stores them using the meter reading
   * service, and then calculates the expected cost for each price plan. It then calls the
   * `calculatedCostForEachPricePlan` method of the `controller` object and asserts that the
   * returned result matches the expected result.
   */
  @Test
  public void shouldCalculateCostForMeterReadingsForEveryPricePlan() {

    ElectricityReading electricityReading =
        new ElectricityReading(Instant.now().minusSeconds(3600), BigDecimal.valueOf(15.0));
    ElectricityReading otherReading =
        new ElectricityReading(Instant.now(), BigDecimal.valueOf(5.0));
    meterReadingService.storeReadings(
        SMART_METER_ID, Arrays.asList(electricityReading, otherReading));

    Map<String, BigDecimal> expectedPricePlanToCost = new ConcurrentHashMap<>();
    expectedPricePlanToCost.put(PRICE_PLAN_1_ID, BigDecimal.valueOf(100.0));
    expectedPricePlanToCost.put(PRICE_PLAN_2_ID, BigDecimal.valueOf(10.0));
    expectedPricePlanToCost.put(PRICE_PLAN_3_ID, BigDecimal.valueOf(20.0));

    Map<String, Object> expected = new ConcurrentHashMap<>();
    expected.put(PricePlanComparatorController.PRICE_PLAN_ID_KEY, PRICE_PLAN_1_ID);
    expected.put(PricePlanComparatorController.PRICE_PLAN_COMPARISONS_KEY, expectedPricePlanToCost);
    assertThat(controller.calculatedCostForEachPricePlan(SMART_METER_ID).getBody())
        .isEqualTo(expected);
  }

  /**
   * Test case to verify that the recommendCheapestPricePlans method returns the expected list of
   * price plans and their corresponding costs when there is no limit for meter usage.
   *
   * <p>This test case creates two electricity readings, stores them using the meterReadingService,
   * and then calls the recommendCheapestPricePlans method of the controller object with a null
   * limit. It asserts that the returned result matches the expected result, which is a list of
   * price plans and their corresponding costs.
   *
   * @throws Exception if an error occurs during the test case.
   */
  @Test
  public void shouldRecommendCheapestPricePlansNoLimitForMeterUsage() throws Exception {

    ElectricityReading electricityReading =
        new ElectricityReading(Instant.now().minusSeconds(1800), BigDecimal.valueOf(35.0));
    ElectricityReading otherReading =
        new ElectricityReading(Instant.now(), BigDecimal.valueOf(3.0));
    meterReadingService.storeReadings(
        SMART_METER_ID, Arrays.asList(electricityReading, otherReading));

    List<Map.Entry<String, BigDecimal>> expectedPricePlanToCost = new ArrayList<>();
    expectedPricePlanToCost.add(
        new AbstractMap.SimpleEntry<>(PRICE_PLAN_2_ID, BigDecimal.valueOf(38.0)));
    expectedPricePlanToCost.add(
        new AbstractMap.SimpleEntry<>(PRICE_PLAN_3_ID, BigDecimal.valueOf(76.0)));
    expectedPricePlanToCost.add(
        new AbstractMap.SimpleEntry<>(PRICE_PLAN_1_ID, BigDecimal.valueOf(380.0)));

    assertThat(controller.recommendCheapestPricePlans(SMART_METER_ID, null).getBody())
        .isEqualTo(expectedPricePlanToCost);
  }

  /**
   * Test case to verify that the recommendCheapestPricePlans method returns the expected list of
   * price plans and their corresponding costs when there is a limit for meter usage.
   *
   * <p>This test case creates two electricity readings, stores them using the meterReadingService,
   * and then calls the recommendCheapestPricePlans method of the controller object with a limit of
   * 2. It asserts that the returned result matches the expected result, which is a list of price
   * plans and their corresponding costs.
   *
   * @throws Exception if an error occurs during the test case.
   */
  @Test
  public void shouldRecommendLimitedCheapestPricePlansForMeterUsage() throws Exception {

    ElectricityReading electricityReading =
        new ElectricityReading(Instant.now().minusSeconds(2700), BigDecimal.valueOf(5.0));
    ElectricityReading otherReading =
        new ElectricityReading(Instant.now(), BigDecimal.valueOf(20.0));
    meterReadingService.storeReadings(
        SMART_METER_ID, Arrays.asList(electricityReading, otherReading));

    List<Map.Entry<String, BigDecimal>> expectedPricePlanToCost = new ArrayList<>();
    expectedPricePlanToCost.add(
        new AbstractMap.SimpleEntry<>(PRICE_PLAN_2_ID, BigDecimal.valueOf(16.7)));
    expectedPricePlanToCost.add(
        new AbstractMap.SimpleEntry<>(PRICE_PLAN_3_ID, BigDecimal.valueOf(33.4)));

    assertThat(controller.recommendCheapestPricePlans(SMART_METER_ID, 2).getBody())
        .isEqualTo(expectedPricePlanToCost);
  }

  /**
   * Test case to verify that the recommendCheapestPricePlans method returns the expected list of
   * price plans and their corresponding costs when there is more than the limit available for the
   * meter usage.
   *
   * <p>This test case creates two electricity readings, stores them using the meterReadingService,
   * and then calls the recommendCheapestPricePlans method of the controller object with a limit of
   * 5. It asserts that the returned result matches the expected result, which is a list of price
   * plans and their corresponding costs.
   *
   * @throws Exception if an error occurs during the test case.
   */
  @Test
  public void shouldRecommendCheapestPricePlansMoreThanLimitAvailableForMeterUsage()
      throws Exception {

    ElectricityReading electricityReading =
        new ElectricityReading(Instant.now().minusSeconds(3600), BigDecimal.valueOf(25.0));
    ElectricityReading otherReading =
        new ElectricityReading(Instant.now(), BigDecimal.valueOf(3.0));
    meterReadingService.storeReadings(
        SMART_METER_ID, Arrays.asList(electricityReading, otherReading));

    List<Map.Entry<String, BigDecimal>> expectedPricePlanToCost = new ArrayList<>();
    expectedPricePlanToCost.add(
        new AbstractMap.SimpleEntry<>(PRICE_PLAN_2_ID, BigDecimal.valueOf(14.0)));
    expectedPricePlanToCost.add(
        new AbstractMap.SimpleEntry<>(PRICE_PLAN_3_ID, BigDecimal.valueOf(28.0)));
    expectedPricePlanToCost.add(
        new AbstractMap.SimpleEntry<>(PRICE_PLAN_1_ID, BigDecimal.valueOf(140.0)));

    assertThat(controller.recommendCheapestPricePlans(SMART_METER_ID, 5).getBody())
        .isEqualTo(expectedPricePlanToCost);
  }

  /**
   * Test case to verify that the controller returns a "not found" status code when given an invalid
   * meter ID.
   */
  @Test
  public void givenNoMatchingMeterIdShouldReturnNotFound() {
    assertThat(controller.calculatedCostForEachPricePlan("not-found").getStatusCode())
        .isEqualTo(HttpStatus.NOT_FOUND);
  }
}
