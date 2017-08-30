package uk.tw.energy.service;

import uk.tw.energy.domain.MeterData;
import uk.tw.energy.domain.Tariff;

import java.math.BigDecimal;

public class CostService {
    public BigDecimal calculateCost(MeterData meterData, Tariff tariff){
        return meterData.getConsumption().multiply(tariff.getUnitRate());
    }
}
