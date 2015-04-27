package com.wazzanau.terdoppio.peerconnection.messages;

public class Request extends PeerMessage {
	
	private final long index;
	private final long begin;
	private final long length;
	
	public Request(long index, long begin, long length) {
		super(PeerMessageType.REQUEST);
		this.index = index;
		this.begin = begin;
		this.length = length;
	}

	public long getIndex() {
		return index;
	}

	public long getBegin() {
		return begin;
	}

	public long getLength() {
		return length;
	}
}
