package uk.tw.energy;

import static java.util.Collections.emptyList;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.PricePlan;
import uk.tw.energy.generator.ElectricityReadingsGenerator;

/** Main class for the Energy application. */
@Configuration
public class SeedingApplicationDataConfiguration {

  private static final String MOST_EVIL_PRICE_PLAN_ID = "price-plan-0";
  private static final String RENEWABLES_PRICE_PLAN_ID = "price-plan-1";
  private static final String STANDARD_PRICE_PLAN_ID = "price-plan-2";

  /**
   * The constructor for the {@link SeedingApplicationDataConfiguration}.
   *
   * <p>It does nothing, but it's constructor makes it a candidate for dependency injection by the
   * Spring framework.
   */
  public SeedingApplicationDataConfiguration() {}

  /**
   * Creates and returns a list of PricePlan objects.
   *
   * @return a list of PricePlan objects
   */
  @Bean
  public List<PricePlan> pricePlans() {
    final List<PricePlan> pricePlans = new ArrayList<>();
    pricePlans.add(
        new PricePlan(
            MOST_EVIL_PRICE_PLAN_ID, "Dr Evil's Dark Energy", BigDecimal.TEN, emptyList()));
    pricePlans.add(
        new PricePlan(
            RENEWABLES_PRICE_PLAN_ID, "The Green Eco", BigDecimal.valueOf(2), emptyList()));
    pricePlans.add(
        new PricePlan(STANDARD_PRICE_PLAN_ID, "Power for Everyone", BigDecimal.ONE, emptyList()));
    return pricePlans;
  }

  /**
   * Creates and returns a ConcurrentHashMap that maps smart meter IDs to their corresponding
   * electricity readings.
   *
   * @return a ConcurrentHashMap containing the mappings between smart meter IDs and electricity
   *     readings.
   */
  @Bean
  public ConcurrentHashMap<String, List<ElectricityReading>> perMeterElectricityReadings() {
    final ConcurrentHashMap<String, List<ElectricityReading>> readings = new ConcurrentHashMap<>();
    final ElectricityReadingsGenerator electricityReadingsGenerator =
        new ElectricityReadingsGenerator();
    smartMeterToPricePlanAccounts()
        .keySet()
        .forEach(
            smartMeterId -> readings.put(smartMeterId, electricityReadingsGenerator.generate(20)));
    return readings;
  }

  /**
   * Creates and returns a ConcurrentHashMap that maps smart meter IDs to their corresponding price
   * plan IDs.
   *
   * @return a ConcurrentHashMap containing the mappings between smart meter IDs and price plan IDs.
   */
  @Bean
  public ConcurrentHashMap<String, String> smartMeterToPricePlanAccounts() {
    final ConcurrentHashMap<String, String> smartMeterToPricePlanAccounts =
        new ConcurrentHashMap<>();
    smartMeterToPricePlanAccounts.put("smart-meter-0", MOST_EVIL_PRICE_PLAN_ID);
    smartMeterToPricePlanAccounts.put("smart-meter-1", RENEWABLES_PRICE_PLAN_ID);
    smartMeterToPricePlanAccounts.put("smart-meter-2", MOST_EVIL_PRICE_PLAN_ID);
    smartMeterToPricePlanAccounts.put("smart-meter-3", STANDARD_PRICE_PLAN_ID);
    smartMeterToPricePlanAccounts.put("smart-meter-4", RENEWABLES_PRICE_PLAN_ID);
    return smartMeterToPricePlanAccounts;
  }

  /**
   * A description of the entire Java function.
   *
   * @param builder description of parameter
   * @return description of return value
   */
  @Bean
  @Primary
  public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
    ObjectMapper objectMapper = builder.createXmlMapper(false).build();
    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    return objectMapper;
  }
}
