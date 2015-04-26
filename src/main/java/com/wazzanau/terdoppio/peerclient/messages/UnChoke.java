package com.wazzanau.terdoppio.peerclient.messages;

public class UnChoke extends PeerMessage {
	
	public final static UnChoke MESSAGE = new UnChoke(); 

	public UnChoke() {
		super(PeerMessageType.UNCHOKE);
	}

}
