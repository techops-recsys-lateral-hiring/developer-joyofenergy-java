package uk.tw.energy.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.MeterReadings;
import uk.tw.energy.exception.ObjectNotFoundException;

@Service
@RequiredArgsConstructor
public class MeterReadingServiceImpl implements MeterReadingService {

	private static final String METER_NOT_FOUND = "No MeterRatings found with ID : [%s]";
	private final Map<String, List<ElectricityReading>> meterAssociatedReadings;

	public List<ElectricityReading> getReadings(String smartMeterId) {
		Optional<List<ElectricityReading>> readings = Optional.ofNullable(meterAssociatedReadings.get(smartMeterId));
		if (readings.isEmpty()) {
			throw new ObjectNotFoundException(String.format(METER_NOT_FOUND, smartMeterId));
		}
		return readings.get();
	}

	public MeterReadings storeReadings(MeterReadings meterReadings) {
		String smartMeterId = meterReadings.getSmartMeterId();
		if (!meterAssociatedReadings.containsKey(smartMeterId)) {
			meterAssociatedReadings.put(smartMeterId, new ArrayList<>());
		}
		meterAssociatedReadings.get(smartMeterId).addAll(meterReadings.getElectricityReadings());
		return meterReadings;
	}
}
