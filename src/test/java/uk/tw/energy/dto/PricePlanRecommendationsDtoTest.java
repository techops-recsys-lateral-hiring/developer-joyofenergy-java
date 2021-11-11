package uk.tw.energy.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.MapEntry.entry;

class PricePlanRecommendationsDtoTest {

    @Test
    void shouldReturnOptionalEmptyForRecommendationsIfNoComparisonsFound() {
        Integer limit = 2;

        Optional<PricePlanRecommendationsDto> result = PricePlanRecommendationsDto.fromComparisons(Collections.emptyMap(), limit);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldConvertFromComparisonsToRecommendationsForGivenLimit() {
        Integer limit = 2;
        String pricePlan1 = "price-plan-1";
        String pricePlan2 = "price-plan-2";
        String pricePlan3 = "price-plan-3";
        Map<String, BigDecimal> pricePlanComparisons = new HashMap<>();
        pricePlanComparisons.put(pricePlan1, BigDecimal.TEN);
        pricePlanComparisons.put(pricePlan2, BigDecimal.valueOf(100));
        pricePlanComparisons.put(pricePlan3, BigDecimal.valueOf(1000));

        Optional<PricePlanRecommendationsDto> recommendations = PricePlanRecommendationsDto.fromComparisons(pricePlanComparisons, limit);

        assertThat(recommendations).isPresent();
        List<Map.Entry<String, BigDecimal>> result = recommendations.get().getRecommendations();
        assertThat(result).hasSize(2);

        assertThat(result).containsExactlyInAnyOrder(entry(pricePlan1, BigDecimal.TEN), entry(pricePlan2, BigDecimal.valueOf(100)));
    }

}
