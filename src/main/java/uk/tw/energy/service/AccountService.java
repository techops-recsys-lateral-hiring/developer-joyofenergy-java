/** Provides a service for looking up the price plan associated with a smart meter. */
package uk.tw.energy.service;

import java.util.Map;
import org.springframework.stereotype.Service;

/** Service for looking up the price plan associated with a smart meter. */
@Service
public class AccountService {

  /** Maps smart meter IDs to their associated price plan IDs. */
  private final Map<String, String> smartMeterToPricePlanAccounts;

  /**
   * Constructs an AccountService with the given map of smart meter IDs to price plan IDs.
   *
   * @param smartMeterToPricePlanAccounts a map of smart meter IDs to price plan IDs
   */
  public AccountService(Map<String, String> smartMeterToPricePlanAccounts) {
    this.smartMeterToPricePlanAccounts = smartMeterToPricePlanAccounts;
  }

  /**
   * Retrieves the price plan ID associated with the given smart meter ID.
   *
   * @param smartMeterId the ID of the smart meter
   * @return the price plan ID associated with the smart meter ID, or null if not found
   */
  public String getPricePlanIdForSmartMeterId(String smartMeterId) {
    return smartMeterToPricePlanAccounts.get(smartMeterId);
  }
}
