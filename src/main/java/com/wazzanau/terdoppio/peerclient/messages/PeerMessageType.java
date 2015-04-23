package com.wazzanau.terdoppio.peerclient.messages;

public enum PeerMessageType {
	KEEP_ALIVE (-1), // actually keep_alive has no id.
	CHOKE (0),
	UNCHOKE (1),
	INTERESTED (2),
	NOT_INTERESTED (3),
	HAVE (4),
	BITFIELD (5),
	REQUEST (6),
	PIECE (7),
	CANCEL (8),
	PORT(9);
		
	public final int id;
	
	PeerMessageType(int id) {
		this.id = id;
	}
	
}
