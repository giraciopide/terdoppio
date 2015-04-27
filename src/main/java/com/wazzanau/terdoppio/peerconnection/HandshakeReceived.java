package com.wazzanau.terdoppio.peerconnection;

public interface HandshakeReceived {	
	public void onHandshakeDone(String protocolString, byte[] reservedBytes, byte[] infoHash, byte[] peerId);
}
