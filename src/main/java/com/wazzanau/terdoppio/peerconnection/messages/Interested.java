package com.wazzanau.terdoppio.peerconnection.messages;

public class Interested extends PeerMessage {
	
	public final static Interested MESSAGE = new Interested();
	
	private Interested() {
		super(PeerMessageType.INTERESTED);
	}

}
