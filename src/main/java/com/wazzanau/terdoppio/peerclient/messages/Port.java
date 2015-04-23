package com.wazzanau.terdoppio.peerclient.messages;

public class Port extends PeerMessage {
	
	private final int port;

	public Port(int pieceIndex) {
		super(PeerMessageType.PORT);
		this.port = pieceIndex;
	}

	public int getPieceIndex() {
		return port;
	}
}
