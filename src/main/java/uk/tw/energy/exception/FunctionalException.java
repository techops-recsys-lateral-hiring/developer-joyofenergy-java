package uk.tw.energy.exception;

public class FunctionalException extends RuntimeException {

	private static final long serialVersionUID = -2684047025374201730L;
	public final String errorMessage;

	public FunctionalException(String message, String errorMessage) {
		super(message);
		this.errorMessage = errorMessage;
	}

}
