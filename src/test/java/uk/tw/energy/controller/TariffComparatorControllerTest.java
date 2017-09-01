package uk.tw.energy.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import uk.tw.energy.controller.TariffComparatorController;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.MeterData;
import uk.tw.energy.domain.Tariff;
import uk.tw.energy.service.CostService;
import uk.tw.energy.service.TariffService;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static java.util.Collections.nCopies;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;


@RunWith(MockitoJUnitRunner.class)
public class TariffComparatorControllerTest {

    @Spy
    private CostService costService = new CostService();
    @Spy
    private TariffService tariffService = new TariffService(singletonList(new Tariff("testSupplier", BigDecimal.ONE, null)));

    private TariffComparatorController controller = new TariffComparatorController(costService, tariffService);


    @Test
    public void shouldCallCostService() {
        ElectricityReading reading = new ElectricityReading(Instant.now(), BigDecimal.ONE);
        MeterData meterData = new MeterData("bob", nCopies(2, reading));

        ResponseEntity<List<BigDecimal>> responseEntity = controller.calculateCostEndpoint(
                meterData);

        assertThat(responseEntity.getBody()).contains(new BigDecimal(0));
    }
}
