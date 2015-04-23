package com.wazzanau.terdoppio.peerclient.messages;

public abstract class PeerMessage {
	
	private final PeerMessageType type;

	public PeerMessage(PeerMessageType type) {
		this.type = type;
	}

	public PeerMessageType getType() {
		return type;
	}
}
