package com.wazzanau.terdoppio.peerclient.messages;

public class NotInterested extends PeerMessage {

	public final static NotInterested MESSAGE = new NotInterested();
	
	public NotInterested() {
		super(PeerMessageType.NOT_INTERESTED);
	}

}
