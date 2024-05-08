/**
 * Unit tests for the {@link PricePlan} class.
 *
 * <p>The {@link PricePlan} class is responsible for holding the details of a price plan, including
 * the base price, peak time multiplier and any exceptional prices.
 */
package uk.tw.energy.domain;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link PricePlan} class.
 *
 * <p>The {@link PricePlan} class is responsible for holding the details of a price plan, including
 * the base price, peak time multiplier and any exceptional prices.
 */
public class PricePlanTest {

  /** Default constructor. */
  public PricePlanTest() {}

  private final String ENERGY_SUPPLIER_NAME = "Energy Supplier Name";

  /**
   * Test case to verify that the energy supplier given in the constructor is returned correctly.
   *
   * <p>This test case creates a new instance of the `PricePlan` class with a specified energy
   * supplier name. It then calls the `getEnergySupplier` method of the `pricePlan` object and
   * asserts that the returned result matches the expected energy supplier name.
   */
  @Test
  public void shouldReturnTheEnergySupplierGivenInTheConstructor() {
    PricePlan pricePlan = new PricePlan(null, ENERGY_SUPPLIER_NAME, null, null);

    assertThat(pricePlan.getEnergySupplier()).isEqualTo(ENERGY_SUPPLIER_NAME);
  }

  /**
   * Test case to verify that the base price is returned correctly given an ordinary date and time.
   *
   * <p>This test case creates a new instance of the `PricePlan` class with a specified base price
   * and a peak time multiplier. It then calls the `getPrice` method of the `pricePlan` object with
   * an ordinary date and time and asserts that the returned price is close to the expected base
   * price.
   *
   * @throws Exception if an error occurs during the test
   */
  @Test
  public void shouldReturnTheBasePriceGivenAnOrdinaryDateTime() throws Exception {
    LocalDateTime normalDateTime = LocalDateTime.of(2017, Month.AUGUST, 31, 12, 0, 0);
    PeakTimeMultiplier peakTimeMultiplier =
        new PeakTimeMultiplier(
            PeriodType.PEAK,
            DayOfWeek.WEDNESDAY,
            BigDecimal.TEN,
            LocalDateTime.of(2024, Month.JANUARY, 1, 20, 0, 0),
            LocalDateTime.of(2024, Month.DECEMBER, 31, 21, 0, 0));
    PricePlan pricePlan =
        new PricePlan(
            "Plan1", ENERGY_SUPPLIER_NAME, BigDecimal.ONE, Arrays.asList(peakTimeMultiplier));

    BigDecimal price = pricePlan.getPrice(normalDateTime);

    assertThat(price).isCloseTo(BigDecimal.ONE, Percentage.withPercentage(1));
  }

  /**
   * Test case to verify that the correct exception price is returned given an exceptional date and
   * time.
   *
   * @throws Exception if an error occurs during the test
   */
  @Test
  public void shouldReturnAnExceptionPriceGivenExceptionalDateTime() throws Exception {
    LocalDateTime exceptionalDateTime = LocalDateTime.of(2017, Month.AUGUST, 30, 23, 0, 0);
    PeakTimeMultiplier peakTimeMultiplier =
        new PeakTimeMultiplier(
            PeriodType.PEAK,
            DayOfWeek.WEDNESDAY,
            BigDecimal.TEN,
            LocalDateTime.of(2017, Month.JANUARY, 1, 00, 0, 0),
            LocalDateTime.of(2017, Month.DECEMBER, 31, 21, 0, 0));
    PricePlan pricePlan =
        new PricePlan(null, null, BigDecimal.ONE, singletonList(peakTimeMultiplier));

    BigDecimal price = pricePlan.getPrice(exceptionalDateTime);

    assertThat(price).isCloseTo(BigDecimal.TEN, Percentage.withPercentage(1));
  }

  /**
   * Test case to verify that the function correctly handles multiple exceptional date times.
   *
   * @throws Exception if an error occurs during the test
   */
  @Test
  public void shouldReceiveMultipleExceptionalDateTimes() throws Exception {
    LocalDateTime exceptionalDateTime = LocalDateTime.of(2017, Month.AUGUST, 30, 23, 0, 0);
    PeakTimeMultiplier peakTimeMultiplier =
        new PeakTimeMultiplier(
            PeriodType.PEAK,
            DayOfWeek.WEDNESDAY,
            BigDecimal.TEN,
            LocalDateTime.of(2017, Month.JANUARY, 1, 0, 0, 0),
            LocalDateTime.of(2017, Month.DECEMBER, 31, 21, 0, 0));
    PeakTimeMultiplier otherPeakTimeMultiplier =
        new PeakTimeMultiplier(
            PeriodType.PEAK,
            DayOfWeek.TUESDAY,
            BigDecimal.TEN,
            LocalDateTime.of(2024, Month.JANUARY, 1, 20, 0, 0),
            LocalDateTime.of(2024, Month.DECEMBER, 31, 21, 0, 0));
    List<PeakTimeMultiplier> peakTimeMultipliers =
        Arrays.asList(peakTimeMultiplier, otherPeakTimeMultiplier);
    PricePlan pricePlan = new PricePlan(null, null, BigDecimal.ONE, peakTimeMultipliers);

    BigDecimal price = pricePlan.getPrice(exceptionalDateTime);

    assertThat(price).isCloseTo(BigDecimal.TEN, Percentage.withPercentage(1));
  }
}
