package uk.tw.energy.controller;

import java.util.List;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.MeterReadings;

public class ValidatorUtil {
    public static boolean validateMeterReading(MeterReadings meterReadings) {
        String smartMeterId = meterReadings.smartMeterId();
        List<ElectricityReading> electricityReadings = meterReadings.electricityReadings();
        return !StringUtils.hasLength(smartMeterId) || CollectionUtils.isEmpty(electricityReadings);
    }
}
