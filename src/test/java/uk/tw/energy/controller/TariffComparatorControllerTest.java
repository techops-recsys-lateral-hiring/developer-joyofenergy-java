package uk.tw.energy.controller;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.Tariff;
import uk.tw.energy.service.MeterReadingService;
import uk.tw.energy.service.TariffService;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TariffComparatorControllerTest {

    private TariffService tariffService;

    private TariffComparatorController controller;
    private MeterReadingService meterReadingService;

    private String tariffName = "test-supplier";
    private String otherTariffName = "best-supplier";

    @Before
    public void setUp() {

        meterReadingService = new MeterReadingService(new HashMap<>());

        Tariff tariff = new Tariff(tariffName, BigDecimal.TEN, null);
        Tariff otherTariff = new Tariff(otherTariffName, BigDecimal.ONE, null);

        List<Tariff> tariffs = Arrays.asList(tariff, otherTariff);
        tariffService = new TariffService(tariffs, meterReadingService);
        controller = new TariffComparatorController(tariffService);

    }


    @Test
    public void shouldCalculateCostForMeterReadingsForEveryTariff() {

        Map<String, BigDecimal> tariffToCost = new HashMap<>();
        tariffToCost.put(tariffName, BigDecimal.valueOf(100));
        tariffToCost.put(otherTariffName, BigDecimal.valueOf(10));

        String meterId = "meter-id";
        ElectricityReading electricityReading = new ElectricityReading(Instant.now().minusSeconds(3600), BigDecimal.valueOf(15.0));
        ElectricityReading otherReading = new ElectricityReading(Instant.now(), BigDecimal.valueOf(5.0));
        meterReadingService.storeReadings(meterId, Arrays.asList(electricityReading, otherReading));

        assertThat(controller.calculatedCostForEachTariff(meterId).getBody()).isEqualTo(tariffToCost);

    }

    @Test
    public void givenNoMatchingMeterIdShouldReturnNotFound() {

        assertThat(controller.calculatedCostForEachTariff("not-found").getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    }

}
