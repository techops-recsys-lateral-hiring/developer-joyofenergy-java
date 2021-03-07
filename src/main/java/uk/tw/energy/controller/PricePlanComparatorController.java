package uk.tw.energy.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import uk.tw.energy.service.AccountService;
import uk.tw.energy.service.PricePlanService;

@RestController
@RequestMapping("/v1/price-plans")
@RequiredArgsConstructor
public class PricePlanComparatorController {

	private final PricePlanService pricePlanService;
	private final AccountService accountService;

	@GetMapping("/comparison/{smartMeterId}")
	public ResponseEntity<Map<String, Object>> calculatedCostForEachPricePlan(@PathVariable String smartMeterId) {
		return ResponseEntity.ok(pricePlanService.calculateCostForEachPricePlan(smartMeterId,
				accountService.getPricePlanIdForSmartMeterId(smartMeterId)));
	}

	@GetMapping("/recommended/{smartMeterId}")
	public ResponseEntity<List<Map.Entry<String, BigDecimal>>> recommendCheapestPricePlans(
			@PathVariable String smartMeterId, @RequestParam(value = "limit", required = false) Integer limit) {
		return ResponseEntity.ok(pricePlanService.recommendedCheapestPricePlan(smartMeterId, limit));
	}
}
