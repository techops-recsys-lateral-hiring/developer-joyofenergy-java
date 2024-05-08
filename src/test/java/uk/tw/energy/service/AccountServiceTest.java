/**
 * Unit tests for the {@link AccountService} class.
 *
 * <p>The {@link AccountService} class is responsible for managing accounts and their associated
 * price plans.
 */
package uk.tw.energy.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link AccountService} class.
 *
 * <p>The {@link AccountService} class is responsible for managing accounts and their associated
 * price plans.
 */
public class AccountServiceTest {

  /** Constructor for {@link AccountServiceTest}. */
  public AccountServiceTest() {}

  private static final String PRICE_PLAN_ID = "price-plan-id";
  private static final String SMART_METER_ID = "smart-meter-id";

  private AccountService accountService;

  /**
   * Sets up the test environment by creating a new instance of the AccountService class with a map
   * containing a single entry mapping a smart meter ID to a price plan ID. @BeforeEach annotation
   * indicates that this method should be executed before each test method in the class.
   */
  @BeforeEach
  public void setUp() {
    Map<String, String> smartMeterToPricePlanAccounts = new HashMap<>();
    smartMeterToPricePlanAccounts.put(SMART_METER_ID, PRICE_PLAN_ID);

    accountService = new AccountService(smartMeterToPricePlanAccounts);
  }

  /**
   * Test case to verify that the AccountService class returns the correct price plan ID for a given
   * smart meter ID.
   *
   * @throws Exception if an error occurs during the test execution
   */
  @Test
  public void givenTheSmartMeterIdReturnsThePricePlanId() throws Exception {
    assertThat(accountService.getPricePlanIdForSmartMeterId(SMART_METER_ID))
        .isEqualTo(PRICE_PLAN_ID);
  }
}
