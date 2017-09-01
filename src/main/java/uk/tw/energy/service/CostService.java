package uk.tw.energy.service;

import org.springframework.stereotype.Service;
import uk.tw.energy.domain.MeterReadings;
import uk.tw.energy.domain.Tariff;

import java.math.BigDecimal;
@Service
public class CostService {
    public BigDecimal calculateCost(MeterReadings meterReadings, Tariff tariff){
        return meterReadings.getConsumption().multiply(tariff.getUnitRate());
    }
}
