package uk.tw.energy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.Tariff;
import uk.tw.energy.generator.ElectricityReadingsGenerator;

import java.math.BigDecimal;
import java.util.*;

import static java.util.Collections.emptyList;

@Configuration
@SuppressWarnings("unused")
public class AppConfiguration {

    private static final String TARIFF_0 = "tariff-0";
    private static final String TARIFF_1 = "tariff-1";
    private static final String TARIFF_2 = "tariff-2";
    private static final String METER_0 = "meter-0";
    private static final String METER_1 = "meter-1";
    private static final String METER_2 = "meter-2";
    private static final String METER_3 = "meter-3";
    private static final String METER_4 = "meter-4";

    @Bean
    public List<Tariff> tariffList(){
        List<Tariff> tariffs = new ArrayList<>();
        tariffs.add(new Tariff(TARIFF_0, BigDecimal.ONE, emptyList()));
        tariffs.add(new Tariff(TARIFF_1, BigDecimal.TEN, emptyList()));
        tariffs.add(new Tariff(TARIFF_2, BigDecimal.valueOf(2), emptyList()));
        return tariffs;
    }

    @Bean
    public Map<String, List<ElectricityReading>> perMeterElectricityReadings() {

        return generateMeterElectricityReadings();
    }

    private Map<String, List<ElectricityReading>> generateMeterElectricityReadings() {
        List<String> meterIds = Arrays.asList(METER_0, METER_1, METER_2, METER_3, METER_4);

        Map<String, List<ElectricityReading>> readings = new HashMap<>();
        ElectricityReadingsGenerator electricityReadingsGenerator = new ElectricityReadingsGenerator();
        meterIds.forEach(meterId -> readings.put(meterId, electricityReadingsGenerator.generate(20)));

        return readings;
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        return objectMapper;
    }
}
