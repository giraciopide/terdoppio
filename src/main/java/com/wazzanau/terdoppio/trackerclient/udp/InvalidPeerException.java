package com.wazzanau.terdoppio.trackerclient.udp;

public class InvalidPeerException extends Exception {

	public InvalidPeerException() {
	}

	public InvalidPeerException(String message) {
		super(message);
	}

	public InvalidPeerException(Throwable cause) {
		super(cause);
	}

	public InvalidPeerException(String message, Throwable cause) {
		super(message, cause);
	}
}
