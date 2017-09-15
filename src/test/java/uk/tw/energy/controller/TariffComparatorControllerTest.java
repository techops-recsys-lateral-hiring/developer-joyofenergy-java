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

    private String TARIFF_1_ID = "test-supplier";
    private String otherTariffName = "best-supplier";
    private static final String METER_ID = "meter-id";


    @Before
    public void setUp() {
        meterReadingService = new MeterReadingService(new HashMap<>());
        Tariff tariff = new Tariff(TARIFF_1_ID, BigDecimal.TEN, null);
        Tariff otherTariff = new Tariff(otherTariffName, BigDecimal.ONE, null);
        List<Tariff> tariffs = Arrays.asList(tariff, otherTariff);
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
        expectedTariffToCost.put(otherTariffName, BigDecimal.valueOf(10.0));

        Map<String,Object> expected = new HashMap<>();
        expected.put(TariffComparatorController.TARIFF_ID_KEY, TARIFF_1_ID);
        expected.put(TariffComparatorController.TARIFF_COMPARISONS_KEY, expectedTariffToCost);
        assertThat(controller.calculatedCostForEachTariff(METER_ID).getBody()).isEqualTo(expected);
    }

    @Test
    public void givenNoMatchingMeterIdShouldReturnNotFound() {
        assertThat(controller.calculatedCostForEachTariff("not-found").getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
