package uk.tw.energy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import uk.tw.energy.domain.Tariff;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Collections.singletonList;

@Configuration
@SuppressWarnings("unused")
public class AppConfiguration {
    @Bean
    public List<Tariff> tariffList(){
       return singletonList(new Tariff("testSupplier", BigDecimal.ONE));
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return objectMapper;
    }
}
