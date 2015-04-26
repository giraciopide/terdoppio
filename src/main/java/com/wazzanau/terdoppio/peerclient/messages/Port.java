package com.wazzanau.terdoppio.peerclient.messages;

public class Port extends PeerMessage {
	
	private final int port;

	public Port(int dhtListenPort) {
		super(PeerMessageType.PORT);
		this.port = dhtListenPort;
	}

	public int getPieceIndex() {
		return port;
	}
}
