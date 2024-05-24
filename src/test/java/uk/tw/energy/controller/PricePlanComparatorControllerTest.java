package uk.tw.energy.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.PricePlan;
import uk.tw.energy.service.AccountService;
import uk.tw.energy.service.MeterReadingService;
import uk.tw.energy.service.PricePlanService;

public class PricePlanComparatorControllerTest {
    private static final String WORST_PLAN_ID = "worst-supplier";
    private static final String BEST_PLAN_ID = "best-supplier";
    private static final String SECOND_BEST_PLAN_ID = "second-best-supplier";
    private static final String SMART_METER_ID = "smart-meter-id";
    private PricePlanComparatorController controller;
    private MeterReadingService meterReadingService;
    private AccountService accountService;

    @BeforeEach
    public void setUp() {
        meterReadingService = new MeterReadingService(new HashMap<>());

        PricePlan pricePlan1 = new PricePlan(WORST_PLAN_ID, null, BigDecimal.TEN, null);
        PricePlan pricePlan2 = new PricePlan(BEST_PLAN_ID, null, BigDecimal.ONE, null);
        PricePlan pricePlan3 = new PricePlan(SECOND_BEST_PLAN_ID, null, BigDecimal.valueOf(2), null);
        List<PricePlan> pricePlans = List.of(pricePlan1, pricePlan2, pricePlan3);
        PricePlanService pricePlanService = new PricePlanService(pricePlans, meterReadingService);

        accountService = new AccountService(Map.of(SMART_METER_ID, WORST_PLAN_ID));

        controller = new PricePlanComparatorController(pricePlanService, accountService);
    }

    @Test
    public void calculatedCostForEachPricePlan_happyPath() {
        var electricityReading = new ElectricityReading(Instant.now().minusSeconds(3600), BigDecimal.valueOf(15.0));
        var otherReading = new ElectricityReading(Instant.now(), BigDecimal.valueOf(5.0));
        meterReadingService.storeReadings(SMART_METER_ID, List.of(electricityReading, otherReading));

        ResponseEntity<Map<String, Object>> response = controller.calculatedCostForEachPricePlan(SMART_METER_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<String, BigDecimal> expectedPricePlanToCost = new HashMap<>();
        expectedPricePlanToCost.put(WORST_PLAN_ID, BigDecimal.valueOf(100.0));
        expectedPricePlanToCost.put(BEST_PLAN_ID, BigDecimal.valueOf(10.0));
        expectedPricePlanToCost.put(SECOND_BEST_PLAN_ID, BigDecimal.valueOf(20.0));
        Map<String, Object> expected = new HashMap<>();
        expected.put(PricePlanComparatorController.PRICE_PLAN_ID_KEY, WORST_PLAN_ID);
        expected.put(PricePlanComparatorController.PRICE_PLAN_COMPARISONS_KEY, expectedPricePlanToCost);
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @Test
    public void calculatedCostForEachPricePlan_noReadings() {
        ResponseEntity<Map<String, Object>> response = controller.calculatedCostForEachPricePlan("not-found");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void recommendCheapestPricePlans_noLimit() {
        var electricityReading = new ElectricityReading(Instant.now().minusSeconds(1800), BigDecimal.valueOf(35.0));
        var otherReading = new ElectricityReading(Instant.now(), BigDecimal.valueOf(3.0));
        meterReadingService.storeReadings(SMART_METER_ID, List.of(electricityReading, otherReading));

        ResponseEntity<List<Map.Entry<String, BigDecimal>>> response =
                controller.recommendCheapestPricePlans(SMART_METER_ID, null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Map.Entry<String, BigDecimal>> expectedPricePlanToCost = new ArrayList<>();
        expectedPricePlanToCost.add(new AbstractMap.SimpleEntry<>(BEST_PLAN_ID, BigDecimal.valueOf(38.0)));
        expectedPricePlanToCost.add(new AbstractMap.SimpleEntry<>(SECOND_BEST_PLAN_ID, BigDecimal.valueOf(76.0)));
        expectedPricePlanToCost.add(new AbstractMap.SimpleEntry<>(WORST_PLAN_ID, BigDecimal.valueOf(380.0)));
        assertThat(response.getBody()).isEqualTo(expectedPricePlanToCost);
    }

    @Test
    public void recommendCheapestPricePlans_withLimit() {
        var electricityReading = new ElectricityReading(Instant.now().minusSeconds(2700), BigDecimal.valueOf(5.0));
        var otherReading = new ElectricityReading(Instant.now(), BigDecimal.valueOf(20.0));
        meterReadingService.storeReadings(SMART_METER_ID, List.of(electricityReading, otherReading));

        ResponseEntity<List<Map.Entry<String, BigDecimal>>> response =
                controller.recommendCheapestPricePlans(SMART_METER_ID, 2);

        List<Map.Entry<String, BigDecimal>> expectedPricePlanToCost = new ArrayList<>();
        expectedPricePlanToCost.add(new AbstractMap.SimpleEntry<>(BEST_PLAN_ID, BigDecimal.valueOf(16.7)));
        expectedPricePlanToCost.add(new AbstractMap.SimpleEntry<>(SECOND_BEST_PLAN_ID, BigDecimal.valueOf(33.4)));
        assertThat(response.getBody()).isEqualTo(expectedPricePlanToCost);
    }

    @Test
    public void recommendCheapestPricePlans_limitHigherThanNumberOfEntries() {
        var reading0 = new ElectricityReading(Instant.now().minusSeconds(3600), BigDecimal.valueOf(25.0));
        var reading1 = new ElectricityReading(Instant.now(), BigDecimal.valueOf(3.0));
        meterReadingService.storeReadings(SMART_METER_ID, List.of(reading0, reading1));

        ResponseEntity<List<Map.Entry<String, BigDecimal>>> response =
                controller.recommendCheapestPricePlans(SMART_METER_ID, 5);

        List<Map.Entry<String, BigDecimal>> expectedPricePlanToCost = new ArrayList<>();
        expectedPricePlanToCost.add(new AbstractMap.SimpleEntry<>(BEST_PLAN_ID, BigDecimal.valueOf(14.0)));
        expectedPricePlanToCost.add(new AbstractMap.SimpleEntry<>(SECOND_BEST_PLAN_ID, BigDecimal.valueOf(28.0)));
        expectedPricePlanToCost.add(new AbstractMap.SimpleEntry<>(WORST_PLAN_ID, BigDecimal.valueOf(140.0)));
        assertThat(response.getBody()).isEqualTo(expectedPricePlanToCost);
    }
}
