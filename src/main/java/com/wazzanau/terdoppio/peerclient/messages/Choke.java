package com.wazzanau.terdoppio.peerclient.messages;

public class Choke extends PeerMessage {
	
	public final static Choke MESSAGE = new Choke();

	private Choke() {
		super(PeerMessageType.CHOKE);
	}

}
