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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = App.class)
public class EndpointTest {

  @Autowired private TestRestTemplate restTemplate;

  private static HttpEntity<MeterReadings> toHttpEntity(MeterReadings meterReadings) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return new HttpEntity<>(meterReadings, headers);
  }

  @Test
  public void shouldStoreReadings() {
    MeterReadings meterReadings = new MeterReadingsBuilder().generateElectricityReadings().build();
    HttpEntity<MeterReadings> entity = toHttpEntity(meterReadings);

    ResponseEntity<String> response =
        restTemplate.postForEntity("/readings/store", entity, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

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

  private void populateReadingsForMeter(String smartMeterId, List<ElectricityReading> data) {
    MeterReadings readings = new MeterReadings(smartMeterId, data);

    HttpEntity<MeterReadings> entity = toHttpEntity(readings);
    restTemplate.postForEntity("/readings/store", entity, String.class);
  }

  @Test
  public void givenInvalidMeterIdShouldReturnNotFound() {
    String invalidMeterId = "nonexistent";
    ResponseEntity<String> response =
        restTemplate.getForEntity("/readings/read/" + invalidMeterId, String.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  public void givenInvalidMeterReadingsShouldReturnBadRequest() {
    MeterReadings invalidReadings = null;
    HttpEntity<MeterReadings> entity = toHttpEntity(invalidReadings);

    ResponseEntity<String> response =
        restTemplate.postForEntity("/readings/store", entity, String.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

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
