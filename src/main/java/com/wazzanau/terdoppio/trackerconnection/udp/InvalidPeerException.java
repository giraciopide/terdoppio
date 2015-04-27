package com.wazzanau.terdoppio.trackerconnection.udp;

public class InvalidPeerException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
