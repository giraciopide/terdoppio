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
	
	public static final int MSG_ID_KEEP_ALIVE = -1; 
	public static final int MSG_ID_CHOKE = 0;
	public static final int MSG_ID_UNCHOKE = 1;
	public static final int MSG_ID_INTERESTED = 2;
	public static final int MSG_ID_NOT_INTERESTED = 3;
	public static final int MSG_ID_HAVE = 4;
	public static final int MSG_ID_BITFIELD = 5;
	public static final int MSG_ID_REQUEST = 6;
	public static final int MSG_ID_PIECE = 7;
	public static final int MSG_ID_CANCEL = 8;
	public static final int MSG_ID_PORT= 9;

	public final int id;

	PeerMessageType(int id) {
		this.id = id;
	}

}
