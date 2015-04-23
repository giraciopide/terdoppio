package com.wazzanau.terdoppio.peerclient.messages;

public class Have extends PeerMessage {
	
	private final long pieceIndex;

	public Have(long pieceIndex) {
		super(PeerMessageType.HAVE);
		this.pieceIndex = pieceIndex;
	}

	public long getPieceIndex() {
		return pieceIndex;
	}
}
