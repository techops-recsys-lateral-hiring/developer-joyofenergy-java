package uk.tw.energy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.MeterReadings;

import java.math.BigDecimal;
import java.time.Instant;

import static java.util.Collections.nCopies;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = App.class)
public class EndpointTest {


    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private ObjectMapper mapper;

    @Test
    public void shouldCalculateAllPrices() throws JsonProcessingException {
        ElectricityReading reading = new ElectricityReading(Instant.now(), BigDecimal.ONE);
        MeterReadings meterReadings = new MeterReadings("bob", nCopies(2, reading));
        HttpEntity<String> entity = getStringHttpEntity(meterReadings);

        ResponseEntity<String> response = restTemplate.postForEntity("/tariffs/compare-all", entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    private HttpEntity<String> getStringHttpEntity(Object object) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String jsonMeterData = mapper.writeValueAsString(object);
        return (HttpEntity<String>) new HttpEntity(jsonMeterData,headers);
    }

    @Test
    public void shouldStoreReadings() throws JsonProcessingException {

        ElectricityReading reading = new ElectricityReading(Instant.now(), BigDecimal.ONE);
        MeterReadings meterReadings = new MeterReadings("bob", nCopies(2, reading));
        HttpEntity<String> entity = getStringHttpEntity(meterReadings);

        ResponseEntity<String> response = restTemplate.postForEntity("/readings/store", entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    }

    @Test
    public void givenMeterIdShouldReturnAMeterReadingAssociatedWithMeterId() throws JsonProcessingException {

        ElectricityReading reading = new ElectricityReading(Instant.now(), BigDecimal.ONE);
        MeterReadings meterReadings = new MeterReadings("bob", nCopies(2, reading));
        HttpEntity<String> entity = getStringHttpEntity(meterReadings);
        restTemplate.postForEntity("/readings/store", entity, String.class);

        ResponseEntity<String> response = restTemplate.getForEntity("/readings/read/bob", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    }


}