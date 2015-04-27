package com.wazzanau.terdoppio.trackerconnection.udp;

public enum Event {
	NONE(0),
	COMPLETED(1),
	STARTED(2),
	STOPPED(3);
		
	private final int value;
	
	Event(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
}
