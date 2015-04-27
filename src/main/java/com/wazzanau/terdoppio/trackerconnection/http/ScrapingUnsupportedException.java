package com.wazzanau.terdoppio.trackerconnection.http;

public class ScrapingUnsupportedException extends Exception {

	private static final long serialVersionUID = 1;

	public ScrapingUnsupportedException() { }

	public ScrapingUnsupportedException(String message) {
		super(message);
	}

	public ScrapingUnsupportedException(Throwable cause) {
		super(cause);
	}

	public ScrapingUnsupportedException(String message, Throwable cause) {
		super(message, cause);

	}
}
