package uk.tw.energy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import uk.tw.energy.App;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.MeterData;

import java.math.BigDecimal;
import java.time.Instant;

import static java.util.Collections.nCopies;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = App.class)
public class ControllerServiceTest {

    private static final String CALCULATE_ENDPOINT = "/tariffs/compare-all";

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private ObjectMapper mapper;

    @Test
    public void shouldCalculateAllPrices() throws JsonProcessingException {
        ElectricityReading reading = new ElectricityReading(Instant.now(), BigDecimal.ONE);
        MeterData meterData = new MeterData("bob", nCopies(2, reading));
        HttpEntity<String> entity = getStringHttpEntity(meterData);

        ResponseEntity<String> response = restTemplate.postForEntity(CALCULATE_ENDPOINT, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    private HttpEntity<String> getStringHttpEntity(Object object) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String jsonMeterData = mapper.writeValueAsString(object);
        return (HttpEntity<String>) new HttpEntity(jsonMeterData,headers);
    }
}