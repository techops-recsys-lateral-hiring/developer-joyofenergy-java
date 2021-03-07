package uk.tw.energy.domain;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class MeterReadings {

	@NotBlank
	private String smartMeterId;
	@NotEmpty
	private List<ElectricityReading> electricityReadings;

}
