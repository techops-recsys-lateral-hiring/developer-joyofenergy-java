/** Controller for comparing price plans. */
package uk.tw.energy.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.tw.energy.service.AccountService;
import uk.tw.energy.service.PricePlanService;

/**
 * REST controller for comparing price plans.
 *
 * <p>Provides endpoints for comparing the cost of electricity consumption between different price
 * plans.
 */
@RestController
@RequestMapping("/price-plans")
public class PricePlanComparatorController {

  /** The key used in the response map for the current price plan ID. */
  public static final String PRICE_PLAN_ID_KEY = "pricePlanId";

  /** The key used in the response map for the price plan comparisons. */
  public static final String PRICE_PLAN_COMPARISONS_KEY = "pricePlanComparisons";

  /** The service for querying price plans. */
  private final PricePlanService pricePlanService;

  /** The service for querying account information. */
  private final AccountService accountService;

  /**
   * Creates a new {@link PricePlanComparatorController}.
   *
   * @param pricePlanService the service for querying price plans
   * @param accountService the service for querying account information
   */
  public PricePlanComparatorController(
      PricePlanService pricePlanService, AccountService accountService) {
    this.pricePlanService = pricePlanService;
    this.accountService = accountService;
  }

  /**
   * Retrieves the calculated cost for each price plan for a given smart meter ID.
   *
   * @param smartMeterId the ID of the smart meter
   * @return a ResponseEntity containing a map with the price plan ID and the consumption costs for
   *     each price plan, or a not found response if the consumption costs are not available
   */
  @GetMapping("/compare-all/{smartMeterId}")
  public ResponseEntity<Map<String, Object>> calculatedCostForEachPricePlan(
      @PathVariable String smartMeterId) {
    String pricePlanId = accountService.getPricePlanIdForSmartMeterId(smartMeterId);
    Optional<Map<String, BigDecimal>> consumptionsForPricePlans =
        pricePlanService.getConsumptionCostOfElectricityReadingsForEachPricePlan(smartMeterId);

    if (!consumptionsForPricePlans.isPresent()) {
      return ResponseEntity.notFound().build();
    }

    Map<String, Object> pricePlanComparisons = new HashMap<>();
    pricePlanComparisons.put(PRICE_PLAN_ID_KEY, pricePlanId);
    pricePlanComparisons.put(PRICE_PLAN_COMPARISONS_KEY, consumptionsForPricePlans.get());

    return consumptionsForPricePlans.isPresent()
        ? ResponseEntity.ok(pricePlanComparisons)
        : ResponseEntity.notFound().build();
  }

  /**
   * Retrieves the cheapest price plans for a given smart meter ID.
   *
   * @param smartMeterId the ID of the smart meter
   * @param limit the maximum number of recommendations to return (optional)
   * @return a ResponseEntity containing a list of Map.Entry objects representing the cheapest price
   *     plans and their corresponding consumption costs, or a not found response if the consumption
   *     costs are not available
   */
  @GetMapping("/recommend/{smartMeterId}")
  public ResponseEntity<List<Map.Entry<String, BigDecimal>>> recommendCheapestPricePlans(
      @PathVariable String smartMeterId,
      @RequestParam(value = "limit", required = false) Integer limit) {
    Optional<Map<String, BigDecimal>> consumptionsForPricePlans =
        pricePlanService.getConsumptionCostOfElectricityReadingsForEachPricePlan(smartMeterId);

    if (!consumptionsForPricePlans.isPresent()) {
      return ResponseEntity.notFound().build();
    }

    List<Map.Entry<String, BigDecimal>> recommendations =
        new ArrayList<>(consumptionsForPricePlans.get().entrySet());
    recommendations.sort(Comparator.comparing(Map.Entry::getValue));

    if (limit != null && limit < recommendations.size()) {
      recommendations = recommendations.subList(0, limit);
    }

    return ResponseEntity.ok(recommendations);
  }
}
