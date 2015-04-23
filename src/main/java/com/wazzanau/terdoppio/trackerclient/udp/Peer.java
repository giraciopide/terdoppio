package com.wazzanau.terdoppio.trackerclient.udp;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class Peer {
	private final byte[] ipAddress;
	private final int tcpPort;
	
	public Peer(byte[] ipAddress, int tcpPort) throws InvalidPeerException {
		this.ipAddress = ipAddress;
		
		if (!isValidIpAddress(ipAddress)) {
			throw new InvalidPeerException("Invalid ip address for peer: " + Arrays.toString(ipAddress));
		}
		
		this.tcpPort = tcpPort;
	}
	
	public byte[] getIpAddress() {
		return ipAddress;
	}
		
	public int getTcpPort() {
		return tcpPort;
	}
	
	// FIXME this is fugly
	private final static boolean isValidIpAddress(byte[] ip) {
		try {
			Inet4Address.getByAddress(ip);
		} catch (UnknownHostException e) {
			return false;
		}
		return true;
	}
	
	public byte[] encode() {
		ByteBuffer buf = ByteBuffer.allocate(6); // 4 bytes for address, 2 for port
		buf.put(ipAddress);
		buf.putShort((short) tcpPort);
		return buf.array();
	}
}
