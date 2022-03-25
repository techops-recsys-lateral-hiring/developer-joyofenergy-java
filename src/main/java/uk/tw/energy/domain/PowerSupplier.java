package uk.tw.energy.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PowerSupplier {

    private final String supplier;
    private final String planName;
    private TariffPrice tariffPrice;

    public PowerSupplier(String planName, String supplier, TariffPrice tariffPrice) {
        this.planName = planName;
        this.supplier = supplier;
        this.tariffPrice = tariffPrice;
    }

    public String getSupplier() {
        return supplier;
    }

    public String getPlanName() {
        return planName;
    }

    public BigDecimal getUnitRate() {
        return tariffPrice.getUnitRate();
    }

    public BigDecimal getPrice(LocalDateTime dateTime) {
        return tariffPrice.getPrice(dateTime);
    }

}
