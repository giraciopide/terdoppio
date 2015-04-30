package com.wazzanau.terdoppio.trackerconnection;

public interface Peer {
	
	/**
	 * Optional
	 * @return
	 */
	String getPeerId();

	byte[] getIpAddress();

	int getTcpPort();

}