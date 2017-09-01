package uk.tw.energy.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import org.springframework.stereotype.Service;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.MeterData;

import java.util.List;

@Service
public class MeterReadingService {

    private ListMultimap<String, ElectricityReading> userAssociatedElectricityReadings = ArrayListMultimap.create();

    public List<ElectricityReading> getReadings(String userId) {

        return userAssociatedElectricityReadings.get(userId);

    }

    public void storeReadings(MeterData meterData) {

        userAssociatedElectricityReadings.putAll(meterData.getMeterId(), meterData.getElectricityReadings());

    }

}
