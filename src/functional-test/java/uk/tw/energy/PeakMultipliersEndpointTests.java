package uk.tw.energy;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
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
import uk.tw.energy.domain.MeterReadings;

/**
 * Integration tests for the peak time multipliers endpoint.
 *
 * <p>This class contains integration tests for the peak time multipliers endpoint. The endpoint is
 * responsible for retrieving and modifying the peak time multipliers for a given price plan.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PeakMultipliersEndpointTests {

  @Autowired private TestRestTemplate restTemplate;
  private HttpHeaders headers;
  private List<Map<String, Object>> multipliers;

  /**
   * Constructor for PeakMultipliersEndpointTests.
   *
   * <p>This constructor is public so that JUnit can instantiate it.
   */
  public PeakMultipliersEndpointTests() {}

  /**
   * Sets up the test environment before each test case.
   *
   * <p>This method initializes the headers and multipliers variables used in the test cases. It
   * sets the content type of the headers to APPLICATION_JSON. The multipliers variable is populated
   * with test data using the createTestMultipliers() method.
   */
  @BeforeEach
  public void setup() {
    headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    multipliers = createTestMultipliers();
  }

  /**
   * Converts the given body into an HttpEntity with the specified headers.
   *
   * @param body the body of the HttpEntity
   * @param headers the headers of the HttpEntity
   * @return the HttpEntity with the specified body and headers
   */
  private <T> HttpEntity<T> toHttpEntity(T body) {
    return new HttpEntity<>(body, headers);
  }

  /**
   * Tests the '/readings/store' endpoint by sending a POST request with generated electricity
   * readings and asserting the response status code.
   */
  @Test
  public void shouldStoreReadings() {
    MeterReadings meterReadings = new MeterReadingsBuilder().generateElectricityReadings().build();
    ResponseEntity<String> response =
        restTemplate.postForEntity("/readings/store", toHttpEntity(meterReadings), String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  /** Tests the behavior of the system when storing invalid readings. */
  @Test
  public void shouldHandleErrorWhenStoringInvalidReadings() {
    ResponseEntity<String> response =
        restTemplate.postForEntity("/readings/store", toHttpEntity(null), String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).contains("error");
  }

  /**
   * Tests the '/price-plans/price-plan-1/peak-multiplier' endpoint by sending a POST request with
   * the given multipliers and asserts that the response status code is OK.
   */
  @Test
  public void shouldPostPeakMultiplier() {
    ResponseEntity<Void> response =
        restTemplate.postForEntity(
            "/price-plans/price-plan-1/peak-multiplier", toHttpEntity(multipliers), Void.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  /**
   * A test to verify that the endpoint '/price-plans/peak-multipliers' returns a map with keys
   * 'price-plan-1' and 'price-plan-2'.
   */
  @Test
  public void shouldGetAllPeakMultipliers() {
    ResponseEntity<Map<String, List<Map<String, Object>>>> response =
        restTemplate.exchange(
            "/price-plans/peak-multipliers",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<>() {});

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull().containsKeys("price-plan-1", "price-plan-2");
  }

  /** A test to get peak multipliers for a specific plan. */
  @Test
  public void shouldGetPeakMultipliersForSpecificPlan() {
    String url = "/price-plans/price-plan-1/peak-multiplier";

    restTemplate.postForEntity(
        "/price-plans/price-plan-1/peak-multiplier", toHttpEntity(multipliers), Void.class);

    ResponseEntity<List<Map<String, Object>>> response =
        restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull().hasSize(2);
    assertThat(response.getBody().get(0)).containsEntry("period", "PEAK");
  }

  /**
   * Creates a list of test multipliers.
   *
   * @return a list of maps representing the test multipliers. Each map contains the following keys:
   *     - "period": the period of the multiplier (PEAK or OFF_PEAK) - "dayOfWeek": the day of the
   *     week when the multiplier is applicable (FRIDAY or MONDAY) - "multiplier": the multiplier
   *     value - "startDateTime": the start date and time of the multiplier - "endDateTime": the end
   *     date and time of the multiplier
   */
  private List<Map<String, Object>> createTestMultipliers() {
    return List.of(
        Map.of(
            "period", "PEAK",
            "dayOfWeek", "FRIDAY",
            "multiplier", 2.0,
            "startDateTime", "2024-01-01 20:00:00",
            "endDateTime", "2024-12-31 21:00:00"),
        Map.of(
            "period", "OFF_PEAK",
            "dayOfWeek", "MONDAY",
            "multiplier", 1.0,
            "startDateTime", "2024-01-01 20:00:00",
            "endDateTime", "2024-12-31 21:00:00"));
  }
}
