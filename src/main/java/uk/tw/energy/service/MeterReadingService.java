package uk.tw.energy.service;

// import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Service;
import uk.tw.energy.domain.ElectricityReading;

@Service
public class MeterReadingService {

  private final ConcurrentHashMap<String, List<ElectricityReading>> meterAssociatedReadings;

  public MeterReadingService(
      ConcurrentHashMap<String, List<ElectricityReading>> meterAssociatedReadings) {
    this.meterAssociatedReadings = meterAssociatedReadings;
  }

  public Optional<List<ElectricityReading>> getReadings(String smartMeterId) {
    return Optional.ofNullable(meterAssociatedReadings.get(smartMeterId));
  }

  public void storeReadings(String smartMeterId, List<ElectricityReading> electricityReadings) {
    meterAssociatedReadings.compute(
        smartMeterId,
        (key, existingList) -> {
          if (existingList == null) {
            return new CopyOnWriteArrayList<>(electricityReadings);
          } else {
            existingList.addAll(electricityReadings);
            return existingList;
          }
        });
  }
}
