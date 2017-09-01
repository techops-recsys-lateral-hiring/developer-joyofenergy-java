package uk.tw.energy.service;

import org.springframework.stereotype.Service;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.MeterData;

import java.util.*;

@Service
public class MeterReadingService {

    private Map<String, List<ElectricityReading>> meterAssociatedReadings = new HashMap<>();

    public Optional<List<ElectricityReading>> getReadings(String meterId) {

        return Optional.ofNullable(meterAssociatedReadings.get(meterId));

    }

    public void storeReadings(MeterData meterData) {

        String meterId = meterData.getMeterId();

        if ( !meterAssociatedReadings.containsKey(meterId) ) {

            meterAssociatedReadings.put(meterId, new ArrayList<>());

        }

        meterAssociatedReadings.get(meterId).addAll(meterData.getElectricityReadings());

    }

}
