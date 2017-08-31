package uk.tw.energy.domain;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class Tariff {
    private final String supplier;
    private final BigDecimal unitRate; // pounds per kWh
    private List<ExceptionalTariff> exceptionalTariffs;

    public Tariff(String supplier, BigDecimal unitRate, List<ExceptionalTariff> exceptionalTariffs) {
        this.supplier = supplier;
        this.unitRate = unitRate;
        this.exceptionalTariffs = exceptionalTariffs;
    }

    public BigDecimal getUnitRate(){
        return unitRate;
    }

    public BigDecimal getPrice(LocalDateTime dateTime) {
        Optional<ExceptionalTariff> exceptionalTariff = exceptionalTariffs.stream()
                .filter(tariff -> tariff.dayOfWeek.equals(dateTime.getDayOfWeek())).findFirst();
        return exceptionalTariff.map(tariff -> unitRate.multiply(tariff.multiplier)).orElse(unitRate);
    }

    static class ExceptionalTariff {

        DayOfWeek dayOfWeek;
        BigDecimal multiplier;

        public ExceptionalTariff(DayOfWeek dayOfWeek, BigDecimal multiplier) {
            this.dayOfWeek = dayOfWeek;
            this.multiplier = multiplier;
        }
    }
}
