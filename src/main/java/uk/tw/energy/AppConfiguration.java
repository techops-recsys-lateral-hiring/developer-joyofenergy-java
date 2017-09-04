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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;

@Configuration
@SuppressWarnings("unused")
public class AppConfiguration {

    @Bean
    public List<Tariff> tariffList(){

        List<Tariff> tariffs = new ArrayList<>();
        tariffs.add(new Tariff("tariff-0", BigDecimal.ONE, emptyList()));
        tariffs.add(new Tariff("tariff-1", BigDecimal.TEN, emptyList()));
        tariffs.add(new Tariff("tariff-2", BigDecimal.valueOf(2), emptyList()));

        return tariffs;

    }

    @Bean
    public Map<String, List<ElectricityReading>> perMeterElectricityReadings() {

        Map<String, List<ElectricityReading>> readings = new HashMap<>();
        ElectricityReadingsGenerator electricityReadingsGenerator = new ElectricityReadingsGenerator();

        for ( int i = 0; i < 5; i++ ) {

            readings.put("meter-" + i, electricityReadingsGenerator.generate(20));

        }

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
