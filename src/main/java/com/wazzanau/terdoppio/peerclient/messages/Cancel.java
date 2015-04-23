package com.wazzanau.terdoppio.peerclient.messages;

public class Cancel extends PeerMessage {
	
	private final long index;
	private final long begin;
	private final long length;
	
	public Cancel(long index, long begin, long length) {
		super(PeerMessageType.CANCEL);
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
