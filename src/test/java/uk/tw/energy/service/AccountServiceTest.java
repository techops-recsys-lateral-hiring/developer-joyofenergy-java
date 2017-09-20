package uk.tw.energy.service;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class AccountServiceTest {

    private static final String TARIFF_ID = "tariff-id";
    private static final String METER_ID = "meter-id";

    private AccountService accountService;

    @Before
    public void setUp() {
        Map<String, String> meterIdsWithTariffs = new HashMap<>();
        meterIdsWithTariffs.put(METER_ID, TARIFF_ID);

        accountService = new AccountService(meterIdsWithTariffs);
    }

    @Test
    public void givenTheMeterIdReturnsTheTariffId() throws Exception {
        assertThat(accountService.getTariffForMeter(METER_ID)).isEqualTo(TARIFF_ID);
    }
}
