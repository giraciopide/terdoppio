package com.wazzanau.terdoppio.peerconnection.messages;

public class KeepAlive extends PeerMessage {
	
	// Message is so simple that we use always the same instance
	public static final KeepAlive MESSAGE = new KeepAlive();
	
	private KeepAlive() {
		super(PeerMessageType.KEEP_ALIVE);
	}

}
