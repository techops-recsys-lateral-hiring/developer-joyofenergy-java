package uk.tw.energy.domain;

import java.math.BigDecimal;

public class Tariff {
    private final String supplier;
    private final BigDecimal unitRate; // pounds per kWh

    public Tariff(String supplier, BigDecimal unitRate) {
       this.supplier = supplier;
       this.unitRate = unitRate;
    }

    public BigDecimal getUnitRate(){
        return unitRate;
    }
}
