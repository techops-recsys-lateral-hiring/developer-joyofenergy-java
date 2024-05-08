package uk.tw.energy.controller;

import java.util.*;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.tw.energy.domain.PeakTimeMultiplier;
import uk.tw.energy.domain.PricePlan;
import uk.tw.energy.service.AccountService;
import uk.tw.energy.service.PricePlanService;

/**
 * REST controller for managing peak time multipliers for price plans.
 *
 * <p>Provides endpoints for adding and retrieving peak time multipliers for price plans.
 */
@RestController
@RequestMapping("/price-plans")
public class PeakOffPeakController {
  /**
   * The key used in the map returned by {@link #getAllPeakTimeMultipliers()} to identify the price
   * plan ID.
   */
  public static final String PRICE_PLAN_ID_KEY = "pricePlanId";

  /**
   * The key used in the map returned by {@link #getAllPeakTimeMultipliers()} to identify the price
   * plan comparisons.
   */
  public static final String PRICE_PLAN_COMPARISONS_KEY = "pricePlanComparisons";

  /** The service for interacting with PricePlans. */
  private final PricePlanService pricePlanService;

  /**
   * Creates a new PeakOffPeakController.
   *
   * @param pricePlanService the service for interacting with PricePlans
   * @param accountService the service for interacting with Accounts
   */
  public PeakOffPeakController(PricePlanService pricePlanService, AccountService accountService) {
    this.pricePlanService = pricePlanService;
  }

  /**
   * Adds peak time multipliers to a specified price plan.
   *
   * @param planName the name of the price plan to add multipliers to
   * @param requests a list of PeakTimeMultiplier objects containing the multiplier details
   * @return a ResponseEntity containing a map with a single key-value pair, where the key is
   *     "message" and the value is "ok"
   */
  @PostMapping("{planName}/peak-multiplier")
  public ResponseEntity<Object> addPeakTimeMultiplier(
      @PathVariable String planName, @RequestBody List<PeakTimeMultiplier> requests) {
    requests.forEach(
        request -> {
          PeakTimeMultiplier multiplier =
              new PeakTimeMultiplier(
                  request.period,
                  request.dayOfWeek,
                  request.multiplier,
                  request.startDateTime,
                  request.endDateTime);
          pricePlanService.addPeakTimeMultiplierToPlan(planName, multiplier);
        });
    Map<String, String> responseMap = Collections.singletonMap("message", "ok");
    return ResponseEntity.ok().body(responseMap);
  }

  /**
   * Gets the list of peak time multipliers for a specific price plan.
   *
   * @param planName the name of the price plan to retrieve multipliers for
   * @return a ResponseEntity containing the list of peak time multipliers, or not found if the plan
   *     does not exist
   */
  @GetMapping("/{planName}/peak-multiplier")
  public ResponseEntity<List<PeakTimeMultiplier>> getPeakTimeMultipliers(
      @PathVariable String planName) {
    Optional<PricePlan> pricePlan = pricePlanService.getPricePlanByName(planName);
    return pricePlan
        .map(p -> ResponseEntity.ok(p.getPeakTimeMultipliers()))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  /**
   * Retrieves all peak time multipliers for each price plan.
   *
   * @return a ResponseEntity containing a map with price plan names as keys and a list of
   *     PeakTimeMultiplier objects as values
   */
  @GetMapping("/peak-multipliers")
  public ResponseEntity<Map<String, List<PeakTimeMultiplier>>> getAllPeakTimeMultipliers() {
    List<PricePlan> plans = pricePlanService.getAllPricePlans();
    Map<String, List<PeakTimeMultiplier>> multipliers = new HashMap<>();
    plans.forEach(
        plan ->
            multipliers.put(
                plan.getPlanName(), copyPeakTimeMultipliers(plan.getPeakTimeMultipliers())));
    return ResponseEntity.ok(multipliers);
  }

  private List<PeakTimeMultiplier> copyPeakTimeMultipliers(List<PeakTimeMultiplier> multipliers) {
    return multipliers.stream()
        .map(
            multiplier ->
                new PeakTimeMultiplier(
                    multiplier.period,
                    multiplier.dayOfWeek,
                    multiplier.multiplier,
                    multiplier.startDateTime,
                    multiplier.endDateTime))
        .collect(Collectors.toList());
  }
}
