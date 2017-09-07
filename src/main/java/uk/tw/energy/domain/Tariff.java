package uk.tw.energy.domain;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class Tariff {

    private final String name;
    private final BigDecimal unitRate; // unit price per kWh
    private List<PeakTimeMultiplier> peakTimeMultipliers;

    public Tariff(String name, BigDecimal unitRate, List<PeakTimeMultiplier> peakTimeMultipliers) {
        this.name = name;
        this.unitRate = unitRate;
        this.peakTimeMultipliers = peakTimeMultipliers;
    }

    public BigDecimal getUnitRate(){
        return unitRate;
    }

    public BigDecimal getPrice(LocalDateTime dateTime) {
        Optional<PeakTimeMultiplier> peakTimeMultiplier = peakTimeMultipliers.stream()
                .filter(tariff -> tariff.dayOfWeek.equals(dateTime.getDayOfWeek())).findFirst();
        return peakTimeMultiplier.map(tariff -> unitRate.multiply(tariff.multiplier)).orElse(unitRate);
    }

    public String getName() {
        return name;
    }

    static class PeakTimeMultiplier {

        DayOfWeek dayOfWeek;
        BigDecimal multiplier;

        public PeakTimeMultiplier(DayOfWeek dayOfWeek, BigDecimal multiplier) {
            this.dayOfWeek = dayOfWeek;
            this.multiplier = multiplier;
        }
    }
}
