package uk.tw.energy.domain;

import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class PowerSupplierTest {

    private final String ENERGY_SUPPLIER_NAME = "Energy Supplier Name";

    @Test
    public void shouldReturnTheEnergySupplierGivenInTheConstructor() {
        PowerSupplier powerSupplier = new PowerSupplier(null, ENERGY_SUPPLIER_NAME, new PlanPrice(null, null));

        assertThat(powerSupplier.getSupplier()).isEqualTo(ENERGY_SUPPLIER_NAME);
    }

    @Test
    public void shouldReturnTheBasePriceGivenAnOrdinaryDateTime() throws Exception {
        LocalDateTime normalDateTime = LocalDateTime.of(2017, Month.AUGUST, 31, 12, 0, 0);
        PlanPrice.PeakTimeMultiplier peakTimeMultiplier = new PlanPrice.PeakTimeMultiplier(DayOfWeek.WEDNESDAY, BigDecimal.TEN);
        PowerSupplier powerSupplier = new PowerSupplier(null, null, new PlanPrice(BigDecimal.ONE, singletonList(peakTimeMultiplier)));

        BigDecimal price = powerSupplier.getPrice(normalDateTime);

        assertThat(price).isCloseTo(BigDecimal.ONE, Percentage.withPercentage(1));
    }

    @Test
    public void shouldReturnAnExceptionPriceGivenExceptionalDateTime() throws Exception {
        LocalDateTime exceptionalDateTime = LocalDateTime.of(2017, Month.AUGUST, 30, 23, 0, 0);
        PlanPrice.PeakTimeMultiplier peakTimeMultiplier = new PlanPrice.PeakTimeMultiplier(DayOfWeek.WEDNESDAY, BigDecimal.TEN);
        PowerSupplier powerSupplier = new PowerSupplier(null, null, new PlanPrice(BigDecimal.ONE, singletonList(peakTimeMultiplier)));

        BigDecimal price = powerSupplier.getPrice(exceptionalDateTime);

        assertThat(price).isCloseTo(BigDecimal.TEN, Percentage.withPercentage(1));
    }

    @Test
    public void shouldReceiveMultipleExceptionalDateTimes() throws Exception {
        LocalDateTime exceptionalDateTime = LocalDateTime.of(2017, Month.AUGUST, 30, 23, 0, 0);
        PlanPrice.PeakTimeMultiplier peakTimeMultiplier = new PlanPrice.PeakTimeMultiplier(DayOfWeek.WEDNESDAY, BigDecimal.TEN);
        PlanPrice.PeakTimeMultiplier otherPeakTimeMultiplier = new PlanPrice.PeakTimeMultiplier(DayOfWeek.TUESDAY, BigDecimal.TEN);
        List<PlanPrice.PeakTimeMultiplier> peakTimeMultipliers = Arrays.asList(peakTimeMultiplier, otherPeakTimeMultiplier);
        PowerSupplier powerSupplier = new PowerSupplier(null, null, new PlanPrice(BigDecimal.ONE, peakTimeMultipliers));

        BigDecimal price = powerSupplier.getPrice(exceptionalDateTime);

        assertThat(price).isCloseTo(BigDecimal.TEN, Percentage.withPercentage(1));
    }
}
