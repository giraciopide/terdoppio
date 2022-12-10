package dev.dimlight.terdoppio.api;

import java.io.IOException;
import java.io.Serial;

public class BeDecodingException extends IOException {

	@Serial
	private static final long serialVersionUID = 1L;

	public BeDecodingException() {
	}

	public BeDecodingException(String message) {
		super(message);
	}

	public BeDecodingException(Throwable cause) {
		super(cause);
	}

	public BeDecodingException(String message, Throwable cause) {
		super(message, cause);
	}
}
