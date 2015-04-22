package com.wazzanau.terdoppio.bencode;

public class DecodingException extends Exception {

	private static final long serialVersionUID = 1L;

	public DecodingException() {
	}

	public DecodingException(String message) {
		super(message);
	}

	public DecodingException(Throwable cause) {
		super(cause);
	}

	public DecodingException(String message, Throwable cause) {
		super(message, cause);
	}
}
