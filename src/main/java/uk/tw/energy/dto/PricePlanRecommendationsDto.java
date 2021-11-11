package uk.tw.energy.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PricePlanRecommendationsDto {

    private List<Map.Entry<String, BigDecimal>> recommendations;

    public PricePlanRecommendationsDto() {}

    public PricePlanRecommendationsDto(List<Map.Entry<String, BigDecimal>> recommendations) {
        this.recommendations = recommendations;
    }

    public static Optional<PricePlanRecommendationsDto> fromComparisons(Map<String, BigDecimal> pricePlanComparisons, Integer limit) {
        if (pricePlanComparisons.isEmpty()) {
            return Optional.empty();
        }
        List<Map.Entry<String, BigDecimal>> recommendations = new ArrayList<>(pricePlanComparisons.entrySet());
        recommendations.sort(Map.Entry.comparingByValue());

        if (limit != null && limit < recommendations.size()) {
            recommendations = recommendations.subList(0, limit);
        }
        return Optional.of(new PricePlanRecommendationsDto(recommendations));
    }

    public List<Map.Entry<String, BigDecimal>> getRecommendations() {
        return recommendations;
    }

}
