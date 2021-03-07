package uk.tw.energy.exception;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
@JsonInclude(Include.NON_NULL)
public class ProblemException {

	private String title;
	private int statusCode;
	private String detail;
	private Map<String, String> errors;

}
