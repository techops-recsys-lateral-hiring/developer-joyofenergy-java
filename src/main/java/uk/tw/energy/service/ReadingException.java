package uk.tw.energy.service;

@SuppressWarnings("serial")
class ReadingException extends RuntimeException {
	ReadingException(String message) {
		super(message);
	}
}