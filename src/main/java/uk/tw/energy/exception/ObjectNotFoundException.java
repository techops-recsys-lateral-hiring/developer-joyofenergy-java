package uk.tw.energy.exception;

public class ObjectNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1160662752158330348L;

	public ObjectNotFoundException(String message) {
		super(message);
	}

}
