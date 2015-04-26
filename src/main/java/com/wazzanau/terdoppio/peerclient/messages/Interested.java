package com.wazzanau.terdoppio.peerclient.messages;

public class Interested extends PeerMessage {
	
	public final static Interested MESSAGE = new Interested();
	
	private Interested() {
		super(PeerMessageType.INTERESTED);
	}

}
