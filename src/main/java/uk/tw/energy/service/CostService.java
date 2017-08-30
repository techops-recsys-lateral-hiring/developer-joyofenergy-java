package uk.tw.energy.service;

import org.springframework.stereotype.Service;
import uk.tw.energy.domain.MeterData;
import uk.tw.energy.domain.Tariff;

import java.math.BigDecimal;
@Service
public class CostService {
    public BigDecimal calculateCost(MeterData meterData, Tariff tariff){
        return meterData.getConsumption().multiply(tariff.getUnitRate());
    }
}
