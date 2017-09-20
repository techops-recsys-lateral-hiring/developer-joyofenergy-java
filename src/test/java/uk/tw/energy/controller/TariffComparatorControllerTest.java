package uk.tw.energy.controller;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.Tariff;
import uk.tw.energy.service.AccountService;
import uk.tw.energy.service.MeterReadingService;
import uk.tw.energy.service.TariffService;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TariffComparatorControllerTest {

    private TariffComparatorController controller;
    private MeterReadingService meterReadingService;
    private AccountService accountService;

    private static final String TARIFF_1_ID = "test-supplier";
    private static final String TARIFF_2_ID = "best-supplier";
    private static final String TARIFF_3_ID = "second-best-supplier";
    private static final String METER_ID = "meter-id";


    @Before
    public void setUp() {
        meterReadingService = new MeterReadingService(new HashMap<>());
        Tariff tariff1 = new Tariff(TARIFF_1_ID, BigDecimal.TEN, null);
        Tariff tariff2 = new Tariff(TARIFF_2_ID, BigDecimal.ONE, null);
        Tariff tariff3 = new Tariff(TARIFF_3_ID, BigDecimal.valueOf(2), null);

        List<Tariff> tariffs = Arrays.asList(tariff1, tariff2, tariff3);
        TariffService tariffService = new TariffService(tariffs, meterReadingService);

        Map<String,String> meterToTariffs = new HashMap<>();
        meterToTariffs.put(METER_ID, TARIFF_1_ID);
        accountService = new AccountService(meterToTariffs);

        controller = new TariffComparatorController(tariffService, accountService);
    }

    @Test
    public void shouldCalculateCostForMeterReadingsForEveryTariff() {

        ElectricityReading electricityReading = new ElectricityReading(Instant.now().minusSeconds(3600), BigDecimal.valueOf(15.0));
        ElectricityReading otherReading = new ElectricityReading(Instant.now(), BigDecimal.valueOf(5.0));
        meterReadingService.storeReadings(METER_ID, Arrays.asList(electricityReading, otherReading));

        Map<String, BigDecimal> expectedTariffToCost = new HashMap<>();
        expectedTariffToCost.put(TARIFF_1_ID, BigDecimal.valueOf(100.0));
        expectedTariffToCost.put(TARIFF_2_ID, BigDecimal.valueOf(10.0));
        expectedTariffToCost.put(TARIFF_3_ID, BigDecimal.valueOf(20.0));

        Map<String,Object> expected = new HashMap<>();
        expected.put(TariffComparatorController.TARIFF_ID_KEY, TARIFF_1_ID);
        expected.put(TariffComparatorController.TARIFF_COMPARISONS_KEY, expectedTariffToCost);
        assertThat(controller.calculatedCostForEachTariff(METER_ID).getBody()).isEqualTo(expected);
    }

    @Test
    public void shouldRecommendCheapestTariffsNoLimitForMeterUsage() throws Exception {

        ElectricityReading electricityReading = new ElectricityReading(Instant.now().minusSeconds(1800), BigDecimal.valueOf(35.0));
        ElectricityReading otherReading = new ElectricityReading(Instant.now(), BigDecimal.valueOf(3.0));
        meterReadingService.storeReadings(METER_ID, Arrays.asList(electricityReading, otherReading));

        List<Map.Entry<String,BigDecimal>> expectedTariffToCost = new ArrayList<>();
        expectedTariffToCost.add(new AbstractMap.SimpleEntry<>(TARIFF_2_ID, BigDecimal.valueOf(38.0)));
        expectedTariffToCost.add(new AbstractMap.SimpleEntry<>(TARIFF_3_ID, BigDecimal.valueOf(76.0)));
        expectedTariffToCost.add(new AbstractMap.SimpleEntry<>(TARIFF_1_ID, BigDecimal.valueOf(380.0)));

        assertThat(controller.recommendCheapestTariffs(METER_ID, null).getBody()).isEqualTo(expectedTariffToCost);
    }


    @Test
    public void shouldRecommendLimitedCheapestTariffsForMeterUsage() throws Exception {

        ElectricityReading electricityReading = new ElectricityReading(Instant.now().minusSeconds(2700), BigDecimal.valueOf(5.0));
        ElectricityReading otherReading = new ElectricityReading(Instant.now(), BigDecimal.valueOf(20.0));
        meterReadingService.storeReadings(METER_ID, Arrays.asList(electricityReading, otherReading));

        List<Map.Entry<String,BigDecimal>> expectedTariffToCost = new ArrayList<>();
        expectedTariffToCost.add(new AbstractMap.SimpleEntry<>(TARIFF_2_ID, BigDecimal.valueOf(16.7)));
        expectedTariffToCost.add(new AbstractMap.SimpleEntry<>(TARIFF_3_ID, BigDecimal.valueOf(33.4)));

        assertThat(controller.recommendCheapestTariffs(METER_ID, 2).getBody()).isEqualTo(expectedTariffToCost);
    }

    @Test
    public void shouldRecommendCheapestTariffsMoreThanLimitAvailableForMeterUsage() throws Exception {

        ElectricityReading electricityReading = new ElectricityReading(Instant.now().minusSeconds(3600), BigDecimal.valueOf(25.0));
        ElectricityReading otherReading = new ElectricityReading(Instant.now(), BigDecimal.valueOf(3.0));
        meterReadingService.storeReadings(METER_ID, Arrays.asList(electricityReading, otherReading));

        List<Map.Entry<String,BigDecimal>> expectedTariffToCost = new ArrayList<>();
        expectedTariffToCost.add(new AbstractMap.SimpleEntry<>(TARIFF_2_ID, BigDecimal.valueOf(14.0)));
        expectedTariffToCost.add(new AbstractMap.SimpleEntry<>(TARIFF_3_ID, BigDecimal.valueOf(28.0)));
        expectedTariffToCost.add(new AbstractMap.SimpleEntry<>(TARIFF_1_ID, BigDecimal.valueOf(140.0)));

        assertThat(controller.recommendCheapestTariffs(METER_ID, 5).getBody()).isEqualTo(expectedTariffToCost);
    }

    @Test
    public void givenNoMatchingMeterIdShouldReturnNotFound() {
        assertThat(controller.calculatedCostForEachTariff("not-found").getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
