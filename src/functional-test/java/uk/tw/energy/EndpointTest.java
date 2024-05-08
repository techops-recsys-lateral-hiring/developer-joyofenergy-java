/**
 * Integration test class for the endpoints in the application.
 *
 * <p>This class uses the {@link SpringBootTest} annotation to start the application with a random
 * port, and the {@link TestRestTemplate} to make HTTP requests to the running application.
 */
package uk.tw.energy;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import uk.tw.energy.builders.MeterReadingsBuilder;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.MeterReadings;

/**
 * Integration test class for the endpoints in the application.
 *
 * <p>This class uses the {@link SpringBootTest} annotation to start the application with a random
 * port, and the {@link TestRestTemplate} to make HTTP requests to the running application.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = App.class)
public class EndpointTest {

  @Autowired private TestRestTemplate restTemplate;

  /** Default constructor required by JUnit. */
  public EndpointTest() {}

  /**
   * Converts a MeterReadings object into an HttpEntity with JSON content type.
   *
   * @param meterReadings the MeterReadings object to be converted
   * @return the HttpEntity containing the meterReadings object with JSON content type
   */
  private static HttpEntity<MeterReadings> toHttpEntity(MeterReadings meterReadings) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return new HttpEntity<>(meterReadings, headers);
  }

  /**
   * Tests the '/readings/store' endpoint by sending a POST request with generated electricity
   * readings and asserting the response status code.
   */
  @Test
  public void shouldStoreReadings() {
    MeterReadings meterReadings = new MeterReadingsBuilder().generateElectricityReadings().build();
    HttpEntity<MeterReadings> entity = toHttpEntity(meterReadings);

    ResponseEntity<String> response =
        restTemplate.postForEntity("/readings/store", entity, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  /**
   * Tests the '/readings/read/{smartMeterId}' endpoint by sending a GET request with a valid smart
   * meter ID and asserting the response status code and body.
   */
  @SuppressWarnings("DataFlowIssue")
  @Test
  public void givenMeterIdShouldReturnAMeterReadingAssociatedWithMeterId() {
    String smartMeterId = "alice";
    List<ElectricityReading> data =
        List.of(
            new ElectricityReading(Instant.parse("2024-04-26T00:00:10.00Z"), new BigDecimal(10)),
            new ElectricityReading(Instant.parse("2024-04-26T00:00:20.00Z"), new BigDecimal(20)),
            new ElectricityReading(Instant.parse("2024-04-26T00:00:30.00Z"), new BigDecimal(30)));
    populateReadingsForMeter(smartMeterId, data);

    ResponseEntity<ElectricityReading[]> response =
        restTemplate.getForEntity("/readings/read/" + smartMeterId, ElectricityReading[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(Arrays.asList(response.getBody())).isEqualTo(data);
  }

  /**
   * Tests the '/price-plans/compare-all/{smartMeterId}' endpoint by sending a GET request with a
   * valid smart meter ID and asserting the response status code and body.
   */
  @Test
  public void shouldCalculateAllPrices() {
    String smartMeterId = "bob";
    List<ElectricityReading> data =
        List.of(
            new ElectricityReading(Instant.parse("2024-04-26T00:00:10.00Z"), new BigDecimal(10)),
            new ElectricityReading(Instant.parse("2024-04-26T00:00:20.00Z"), new BigDecimal(20)),
            new ElectricityReading(Instant.parse("2024-04-26T00:00:30.00Z"), new BigDecimal(30)));
    populateReadingsForMeter(smartMeterId, data);

    ResponseEntity<CompareAllResponse> response =
        restTemplate.getForEntity(
            "/price-plans/compare-all/" + smartMeterId, CompareAllResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody())
        .isEqualTo(
            new CompareAllResponse(
                Map.of("price-plan-0", 36000, "price-plan-1", 7200, "price-plan-2", 3600), null));
  }

  /**
   * Tests the '/price-plans/recommend/{smartMeterId}' endpoint by sending a GET request with a
   * valid smart meter ID and asserting the response body.
   */
  @SuppressWarnings("rawtypes")
  @Test
  public void givenMeterIdAndLimitShouldReturnRecommendedCheapestPricePlans() {
    String smartMeterId = "jane";
    List<ElectricityReading> data =
        List.of(
            new ElectricityReading(Instant.parse("2024-04-26T00:00:10.00Z"), new BigDecimal(10)),
            new ElectricityReading(Instant.parse("2024-04-26T00:00:20.00Z"), new BigDecimal(20)),
            new ElectricityReading(Instant.parse("2024-04-26T00:00:30.00Z"), new BigDecimal(30)));
    populateReadingsForMeter(smartMeterId, data);

    ResponseEntity<Map[]> response =
        restTemplate.getForEntity(
            "/price-plans/recommend/" + smartMeterId + "?limit=2", Map[].class);

    assertThat(response.getBody())
        .containsExactly(Map.of("price-plan-2", 3600), Map.of("price-plan-1", 7200));
  }

  /**
   * Populates readings for a specific smart meter with the given data.
   *
   * @param smartMeterId the ID of the smart meter
   * @param data the list of electricity readings to populate
   */
  private void populateReadingsForMeter(String smartMeterId, List<ElectricityReading> data) {
    MeterReadings readings = new MeterReadings(smartMeterId, data);

    HttpEntity<MeterReadings> entity = toHttpEntity(readings);
    restTemplate.postForEntity("/readings/store", entity, String.class);
  }

  /**
   * Tests the '/readings/read/{invalidMeterId}' endpoint by sending a GET request with an invalid
   * meter ID and asserting the response status code.
   */
  @Test
  public void givenInvalidMeterIdShouldReturnNotFound() {
    String invalidMeterId = "nonexistent";
    ResponseEntity<String> response =
        restTemplate.getForEntity("/readings/read/" + invalidMeterId, String.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  /**
   * Tests the '/readings/store' endpoint by sending a POST request with null meter readings and
   * asserting the response status code.
   */
  @Test
  public void givenInvalidMeterReadingsShouldReturnBadRequest() {
    MeterReadings invalidReadings = null;
    HttpEntity<MeterReadings> entity = toHttpEntity(invalidReadings);

    ResponseEntity<String> response =
        restTemplate.postForEntity("/readings/store", entity, String.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  /** Tests the concurrent handling of meter reading updates. */
  @Test
  public void shouldHandleConcurrentMeterReadingUpdates() {
    final String meterId = "concurrent-meter";
    final int numberOfThreads = 5000;
    ExecutorService executor = Executors.newFixedThreadPool(50);
    CountDownLatch latch = new CountDownLatch(numberOfThreads);

    Runnable postingTask =
        () -> {
          try {
            MeterReadings readings =
                new MeterReadings(
                    meterId, List.of(new ElectricityReading(Instant.now(), BigDecimal.TEN)));
            HttpEntity<MeterReadings> entity = toHttpEntity(readings);
            restTemplate.postForEntity("/readings/store", entity, String.class);
          } finally {
            latch.countDown(); // Garante que o latch é decrementado mesmo em caso de falha
          }
        };

    IntStream.range(0, numberOfThreads).forEach(i -> executor.submit(postingTask));
    executor.shutdown();
    try {
      if (!executor.awaitTermination(1, TimeUnit.HOURS)) {
        executor.shutdownNow();
      }
      latch.await();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    ResponseEntity<List<ElectricityReading>> response =
        restTemplate.exchange(
            "/readings/read/" + meterId,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<ElectricityReading>>() {});

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).hasSize(numberOfThreads);
  }

  record CompareAllResponse(Map<String, Integer> pricePlanComparisons, String pricePlanId) {}
}
