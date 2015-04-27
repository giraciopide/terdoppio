package com.wazzanau.terdoppio.peerconnection.messages;

public class Piece extends PeerMessage {
	
	private final long index;
	private final long begin;
	private final byte[] data;
	
	public Piece(long index, long begin, byte[] data) {
		super(PeerMessageType.PIECE);
		this.index = index;
		this.begin = begin;
		this.data = data;
	}

	public long getIndex() {
		return index;
	}

	public long getBegin() {
		return begin;
	}

	public byte[] getData() {
		return data;
	}
}
