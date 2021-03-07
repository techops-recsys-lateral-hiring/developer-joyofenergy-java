package uk.tw.energy.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler {

	private static final String PROJECT_TITLE = "JOI-ENERGY";
	private static final String GENERAL_EXCEPTION = "Service Execution Exception";
	private static final String GENERAL_EXCEPTION_DETAIL = "Please try again and contact developer if error persists.";
	private static final String FUNCTIONAL_EXCEPTION = "Functionality not achieved";
	private static final String INVALID_BODY_EXCEPTION = "Constraint Violation";
	private static final String JOINER = " / ";

	@ExceptionHandler(FunctionalException.class)
	public ResponseEntity<ProblemException> handleException(FunctionalException ex) {
		return new ResponseEntity<ProblemException>(
				ProblemException.builder().statusCode(HttpStatus.BAD_REQUEST.value())
						.title(PROJECT_TITLE + JOINER + FUNCTIONAL_EXCEPTION).detail(ex.errorMessage).build(),
				HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ObjectNotFoundException.class)
	public ResponseEntity<ProblemException> handleException(ObjectNotFoundException ex) {
		return new ResponseEntity<ProblemException>(ProblemException.builder().statusCode(HttpStatus.NOT_FOUND.value())
				.title(PROJECT_TITLE).detail(ex.getMessage()).build(), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ProblemException> handleException(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});
		return new ResponseEntity<ProblemException>(
				ProblemException.builder().statusCode(HttpStatus.BAD_REQUEST.value()).title(PROJECT_TITLE)
						.detail(INVALID_BODY_EXCEPTION).errors(errors).build(),
				HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ProblemException> handleException(Exception ex) {
		return new ResponseEntity<ProblemException>(
				ProblemException.builder().title(PROJECT_TITLE + JOINER + GENERAL_EXCEPTION)
						.detail(GENERAL_EXCEPTION_DETAIL).statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value()).build(),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
