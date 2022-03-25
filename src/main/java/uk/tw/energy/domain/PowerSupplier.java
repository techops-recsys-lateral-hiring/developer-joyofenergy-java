package uk.tw.energy.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PowerSupplier {

    private final String supplier;
    private final String planName;
    private PlanPrice planPrice;

    public PowerSupplier(String planName, String supplier, PlanPrice planPrice) {
        this.planName = planName;
        this.supplier = supplier;
        this.planPrice = planPrice;
    }

    public String getSupplier() {
        return supplier;
    }

    public String getPlanName() {
        return planName;
    }

    public BigDecimal getUnitRate() {
        return planPrice.getUnitRate();
    }

    public BigDecimal getPrice(LocalDateTime dateTime) {
        return planPrice.getPrice(dateTime);
    }

}
