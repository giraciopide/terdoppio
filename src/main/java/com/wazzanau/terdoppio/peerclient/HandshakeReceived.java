package com.wazzanau.terdoppio.peerclient;

public interface HandshakeReceived {	
	public void onHandshakeDone(String protocolString, byte[] reservedBytes, byte[] infoHash, byte[] peerId);
}
