package uk.tw.energy.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.MeterReadings;
import uk.tw.energy.service.MeterReadingService;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class MeterReadingController {

	private final MeterReadingService meterReadingService;

	@PostMapping("/readings")
	public ResponseEntity<MeterReadings> storeReadings(@Validated @RequestBody MeterReadings meterReadings) {
		return ResponseEntity.status(HttpStatus.CREATED).body(meterReadingService.storeReadings(meterReadings));
	}

	@GetMapping("/readings/{smartMeterId}")
	public ResponseEntity<List<ElectricityReading>> readReadings(@PathVariable String smartMeterId) {
		return ResponseEntity.ok(meterReadingService.getReadings(smartMeterId));
	}
}
