package com.wazzanau.terdoppio.trackerconnection.http;

public class BadTrackerUrlException extends Exception {

	private static final long serialVersionUID = 1L;

	public BadTrackerUrlException() {
	}

	public BadTrackerUrlException(String message) {
		super(message);
	}

	public BadTrackerUrlException(Throwable cause) {
		super(cause);
	}

	public BadTrackerUrlException(String message, Throwable cause) {
		super(message, cause);
	}
}
