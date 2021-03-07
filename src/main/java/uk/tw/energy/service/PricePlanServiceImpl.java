package uk.tw.energy.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.PricePlan;
import uk.tw.energy.exception.ObjectNotFoundException;

@Service
@RequiredArgsConstructor
public class PricePlanServiceImpl implements PricePlanService {

	public static final String PRICE_PLAN_ID_KEY = "pricePlanId";
	public static final String PRICE_PLAN_COMPARISONS_KEY = "pricePlanComparisons";
	private static final String PRICE_PLAN_NOT_AVAILABLE = "No Defined Price Plan Available";
	private final List<PricePlan> pricePlans;
	private final MeterReadingService meterReadingService;

	public Map<String, Object> calculateCostForEachPricePlan(String smartMeterId, String pricePlanId) {
		Map<String, BigDecimal> consumptionsForPricePlans = getConsumptionCostOfElectricityReadingsForEachPricePlan(
				smartMeterId);
		Map<String, Object> pricePlanComparisons = new HashMap<>();
		pricePlanComparisons.put(PRICE_PLAN_ID_KEY, pricePlanId);
		pricePlanComparisons.put(PRICE_PLAN_COMPARISONS_KEY, consumptionsForPricePlans);
		return pricePlanComparisons;
	}

	public List<Map.Entry<String, BigDecimal>> recommendedCheapestPricePlan(String smartMeterId, Integer limit) {
		Map<String, BigDecimal> consumptionsForPricePlans = getConsumptionCostOfElectricityReadingsForEachPricePlan(
				smartMeterId);
		List<Map.Entry<String, BigDecimal>> recommendations = new ArrayList<>(consumptionsForPricePlans.entrySet());
		recommendations.sort(Comparator.comparing(Map.Entry::getValue));
		if (limit != null && limit < recommendations.size()) {
			recommendations = recommendations.subList(0, limit);
		}
		return recommendations;
	}

	private Map<String, BigDecimal> getConsumptionCostOfElectricityReadingsForEachPricePlan(String smartMeterId) {
		try {
			List<ElectricityReading> electricityReadings = meterReadingService.getReadings(smartMeterId);
			Optional<Map<String, BigDecimal>> consumptionCost = Optional.of(pricePlans.stream()
					.collect(Collectors.toMap(PricePlan::getPlanName, t -> calculateCost(electricityReadings, t))));
			if (consumptionCost.isEmpty()) {
				throw new ObjectNotFoundException(PRICE_PLAN_NOT_AVAILABLE);
			}
			return consumptionCost.get();
		} catch (ObjectNotFoundException e) {
			throw e;
		}
	}

}
