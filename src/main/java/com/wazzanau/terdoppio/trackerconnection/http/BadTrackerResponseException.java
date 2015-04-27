package com.wazzanau.terdoppio.trackerconnection.http;

public class BadTrackerResponseException extends Exception {

	private static final long serialVersionUID = 1L;

	public BadTrackerResponseException() {
	}

	public BadTrackerResponseException(String message) {
		super(message);
	}

	public BadTrackerResponseException(Throwable cause) {
		super(cause);
	}

	public BadTrackerResponseException(String message, Throwable cause) {
		super(message, cause);
	}
}
