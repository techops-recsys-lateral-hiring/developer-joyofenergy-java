package uk.tw.energy;

import static java.util.Collections.emptyList;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.PricePlan;
import uk.tw.energy.generatorUtil.ElectricityReadingsGeneratorUtil;

@Configuration
public class SeedingApplicationDataConfiguration {

	private static final String MOST_EVIL_PRICE_PLAN_ID = "price-plan-0";
	private static final String RENEWABLES_PRICE_PLAN_ID = "price-plan-1";
	private static final String STANDARD_PRICE_PLAN_ID = "price-plan-2";
	private static final String DR_EVIL_NAME = "Dr Evil's Dark Energy";
	private static final String GREEN_ECO_NAME = "The Green Eco";
	private static final String POWER_NAME = "Power for Everyone";
	private static final String SMART_METER_0 = "smart-meter-0";
	private static final String SMART_METER_1 = "smart-meter-1";
	private static final String SMART_METER_2 = "smart-meter-2";
	private static final String SMART_METER_3 = "smart-meter-3";
	private static final String SMART_METER_4 = "smart-meter-4";

	@Bean
	public List<PricePlan> pricePlans() {
		final List<PricePlan> pricePlans = new ArrayList<>();
		pricePlans.add(new PricePlan(MOST_EVIL_PRICE_PLAN_ID, DR_EVIL_NAME, BigDecimal.TEN, emptyList()));
		pricePlans.add(new PricePlan(RENEWABLES_PRICE_PLAN_ID, GREEN_ECO_NAME, BigDecimal.valueOf(2), emptyList()));
		pricePlans.add(new PricePlan(STANDARD_PRICE_PLAN_ID, POWER_NAME, BigDecimal.ONE, emptyList()));
		return pricePlans;
	}

	@Bean
	public Map<String, List<ElectricityReading>> perMeterElectricityReadings() {
		final Map<String, List<ElectricityReading>> readings = new HashMap<>();
		smartMeterToPricePlanAccounts().keySet()
				.forEach(smartMeterId -> readings.put(smartMeterId, ElectricityReadingsGeneratorUtil.generate(20)));
		return readings;
	}

	@Bean
	public Map<String, String> smartMeterToPricePlanAccounts() {
		final Map<String, String> smartMeterToPricePlanAccounts = new HashMap<>();
		smartMeterToPricePlanAccounts.put(SMART_METER_0, MOST_EVIL_PRICE_PLAN_ID);
		smartMeterToPricePlanAccounts.put(SMART_METER_1, RENEWABLES_PRICE_PLAN_ID);
		smartMeterToPricePlanAccounts.put(SMART_METER_2, MOST_EVIL_PRICE_PLAN_ID);
		smartMeterToPricePlanAccounts.put(SMART_METER_3, STANDARD_PRICE_PLAN_ID);
		smartMeterToPricePlanAccounts.put(SMART_METER_4, RENEWABLES_PRICE_PLAN_ID);
		return smartMeterToPricePlanAccounts;
	}

	@Bean
	@Primary
	public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
		ObjectMapper objectMapper = builder.createXmlMapper(false).build();
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		return objectMapper;
	}
}
