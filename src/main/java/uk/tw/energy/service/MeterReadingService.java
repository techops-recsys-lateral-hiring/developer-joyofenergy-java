package uk.tw.energy.service;

import java.util.List;

import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.MeterReadings;

public interface MeterReadingService {

	public List<ElectricityReading> getReadings(String smartMeterId);

	public MeterReadings storeReadings(MeterReadings meterReadings);

}
