package uk.tw.energy.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.PowerSupplier;
import uk.tw.energy.domain.PlanPrice;
import uk.tw.energy.service.AccountService;
import uk.tw.energy.service.CostCalculationService;
import uk.tw.energy.service.MeterReadingService;
import uk.tw.energy.service.PowerSupplierComparatorService;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class PowerSupplierComparatorControllerTest {

    private static final String PRICE_PLAN_1_ID = "test-supplier";
    private static final String PRICE_PLAN_2_ID = "best-supplier";
    private static final String PRICE_PLAN_3_ID = "second-best-supplier";
    private static final String SMART_METER_ID = "smart-meter-id";
    private PowerSupplierComparatorController controller;
    private CostCalculationService costCalculationService;
    private MeterReadingService meterReadingService;
    private AccountService accountService;

    @BeforeEach
    public void setUp() {
        meterReadingService = new MeterReadingService(new HashMap<>());
        PowerSupplier powerSupplier1 = new PowerSupplier(PRICE_PLAN_1_ID, null, new PlanPrice(BigDecimal.TEN, null));
        PowerSupplier powerSupplier2 = new PowerSupplier(PRICE_PLAN_2_ID, null, new PlanPrice(BigDecimal.ONE, null));
        PowerSupplier powerSupplier3 = new PowerSupplier(PRICE_PLAN_3_ID, null, new PlanPrice(BigDecimal.valueOf(2), null));

        costCalculationService = new CostCalculationService();

        List<PowerSupplier> powerSuppliers = Arrays.asList(powerSupplier1, powerSupplier2, powerSupplier3);
        PowerSupplierComparatorService comparatorService = new PowerSupplierComparatorService(powerSuppliers, meterReadingService);
        comparatorService.setCostCalculationService(costCalculationService);

        Map<String, String> meterToTariffs = new HashMap<>();
        meterToTariffs.put(SMART_METER_ID, PRICE_PLAN_1_ID);
        accountService = new AccountService(meterToTariffs);

        controller = new PowerSupplierComparatorController(comparatorService, accountService);
    }

    @Test
    public void shouldCalculateCostForMeterReadingsForEveryPricePlan() {

        ElectricityReading electricityReading = new ElectricityReading(Instant.now().minusSeconds(3600), BigDecimal.valueOf(15.0));
        ElectricityReading otherReading = new ElectricityReading(Instant.now(), BigDecimal.valueOf(5.0));
        meterReadingService.storeReadings(SMART_METER_ID, Arrays.asList(electricityReading, otherReading));

        Map<String, BigDecimal> expectedPricePlanToCost = new HashMap<>();
        expectedPricePlanToCost.put(PRICE_PLAN_1_ID, BigDecimal.valueOf(100.0));
        expectedPricePlanToCost.put(PRICE_PLAN_2_ID, BigDecimal.valueOf(10.0));
        expectedPricePlanToCost.put(PRICE_PLAN_3_ID, BigDecimal.valueOf(20.0));

        Map<String, Object> expected = new HashMap<>();
        expected.put(PowerSupplierComparatorController.PRICE_PLAN_ID_KEY, PRICE_PLAN_1_ID);
        expected.put(PowerSupplierComparatorController.PRICE_PLAN_COMPARISONS_KEY, expectedPricePlanToCost);
        assertThat(controller.calculatedCostForEachPricePlan(SMART_METER_ID).getBody()).isEqualTo(expected);
    }

    @Test
    public void shouldRecommendCheapestPricePlansNoLimitForMeterUsage() throws Exception {

        ElectricityReading electricityReading = new ElectricityReading(Instant.now().minusSeconds(1800), BigDecimal.valueOf(35.0));
        ElectricityReading otherReading = new ElectricityReading(Instant.now(), BigDecimal.valueOf(3.0));
        meterReadingService.storeReadings(SMART_METER_ID, Arrays.asList(electricityReading, otherReading));

        List<Map.Entry<String, BigDecimal>> expectedPricePlanToCost = new ArrayList<>();
        expectedPricePlanToCost.add(new AbstractMap.SimpleEntry<>(PRICE_PLAN_2_ID, BigDecimal.valueOf(38.0)));
        expectedPricePlanToCost.add(new AbstractMap.SimpleEntry<>(PRICE_PLAN_3_ID, BigDecimal.valueOf(76.0)));
        expectedPricePlanToCost.add(new AbstractMap.SimpleEntry<>(PRICE_PLAN_1_ID, BigDecimal.valueOf(380.0)));

        assertThat(controller.recommendCheapestPlans(SMART_METER_ID, null).getBody()).isEqualTo(expectedPricePlanToCost);
    }


    @Test
    public void shouldRecommendLimitedCheapestPricePlansForMeterUsage() throws Exception {

        ElectricityReading electricityReading = new ElectricityReading(Instant.now().minusSeconds(2700), BigDecimal.valueOf(5.0));
        ElectricityReading otherReading = new ElectricityReading(Instant.now(), BigDecimal.valueOf(20.0));
        meterReadingService.storeReadings(SMART_METER_ID, Arrays.asList(electricityReading, otherReading));

        List<Map.Entry<String, BigDecimal>> expectedPricePlanToCost = new ArrayList<>();
        expectedPricePlanToCost.add(new AbstractMap.SimpleEntry<>(PRICE_PLAN_2_ID, BigDecimal.valueOf(16.7)));
        expectedPricePlanToCost.add(new AbstractMap.SimpleEntry<>(PRICE_PLAN_3_ID, BigDecimal.valueOf(33.4)));

        assertThat(controller.recommendCheapestPlans(SMART_METER_ID, 2).getBody()).isEqualTo(expectedPricePlanToCost);
    }

    @Test
    public void shouldRecommendCheapestPricePlansMoreThanLimitAvailableForMeterUsage() throws Exception {

        ElectricityReading electricityReading = new ElectricityReading(Instant.now().minusSeconds(3600), BigDecimal.valueOf(25.0));
        ElectricityReading otherReading = new ElectricityReading(Instant.now(), BigDecimal.valueOf(3.0));
        meterReadingService.storeReadings(SMART_METER_ID, Arrays.asList(electricityReading, otherReading));

        List<Map.Entry<String, BigDecimal>> expectedPricePlanToCost = new ArrayList<>();
        expectedPricePlanToCost.add(new AbstractMap.SimpleEntry<>(PRICE_PLAN_2_ID, BigDecimal.valueOf(14.0)));
        expectedPricePlanToCost.add(new AbstractMap.SimpleEntry<>(PRICE_PLAN_3_ID, BigDecimal.valueOf(28.0)));
        expectedPricePlanToCost.add(new AbstractMap.SimpleEntry<>(PRICE_PLAN_1_ID, BigDecimal.valueOf(140.0)));

        assertThat(controller.recommendCheapestPlans(SMART_METER_ID, 5).getBody()).isEqualTo(expectedPricePlanToCost);
    }

    @Test
    public void givenNoMatchingMeterIdShouldReturnNotFound() {
        assertThat(controller.calculatedCostForEachPricePlan("not-found").getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
