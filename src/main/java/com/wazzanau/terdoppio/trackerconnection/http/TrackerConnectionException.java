package com.wazzanau.terdoppio.trackerconnection.http;

public class TrackerConnectionException extends Exception {

	private static final long serialVersionUID = 1L;

	public TrackerConnectionException() {
	}

	public TrackerConnectionException(String message) {
		super(message);
	}

	public TrackerConnectionException(Throwable cause) {
		super(cause);
	}

	public TrackerConnectionException(String message, Throwable cause) {
		super(message, cause);
	}
}
