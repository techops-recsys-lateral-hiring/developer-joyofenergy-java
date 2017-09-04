package uk.tw.energy.service;

import org.springframework.stereotype.Service;
import uk.tw.energy.domain.ElectricityReading;

import java.util.*;

@Service
public class MeterReadingService {

    private Map<String, List<ElectricityReading>> meterAssociatedReadings;

    public MeterReadingService(Map<String, List<ElectricityReading>> meterAssociatedReadings) {

        this.meterAssociatedReadings = meterAssociatedReadings;

    }

    public Optional<List<ElectricityReading>> getReadings(String meterId) {

        return Optional.ofNullable(meterAssociatedReadings.get(meterId));

    }

    public void storeReadings(String meterId, List<ElectricityReading> electricityReadings) {

        if ( !meterAssociatedReadings.containsKey(meterId) ) {

            meterAssociatedReadings.put(meterId, new ArrayList<>());

        }

        meterAssociatedReadings.get(meterId).addAll(electricityReadings);

    }

}
