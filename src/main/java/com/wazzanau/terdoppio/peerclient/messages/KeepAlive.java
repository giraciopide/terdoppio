package com.wazzanau.terdoppio.peerclient.messages;

public class KeepAlive extends PeerMessage {

	public KeepAlive() {
		super(PeerMessageType.KEEP_ALIVE);
	}

}
