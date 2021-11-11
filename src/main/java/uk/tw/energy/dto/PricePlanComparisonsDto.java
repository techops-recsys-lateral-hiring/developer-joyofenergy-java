package uk.tw.energy.dto;

import java.math.BigDecimal;
import java.util.Map;

public class PricePlanComparisonsDto {

    private String pricePlanId;

    private Map<String, BigDecimal> pricePlanComparisons;

    public PricePlanComparisonsDto() {}

    public PricePlanComparisonsDto(String pricePlanId, Map<String, BigDecimal> pricePlanComparisons) {
        this.pricePlanId = pricePlanId;
        this.pricePlanComparisons = pricePlanComparisons;
    }

    public String getPricePlanId() {
        return pricePlanId;
    }

    public Map<String, BigDecimal> getPricePlanComparisons() {
        return pricePlanComparisons;
    }

}
