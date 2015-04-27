package com.wazzanau.terdoppio.trackerconnection.udp;

import java.nio.ByteBuffer;


/**
 * Represents the client connect request.
 */
public class ConnectRequest {
	private final long connectionId = UdpTrackerProtocol.CONNECT_REQUEST_CONNECTION_ID;
	private final int action = UdpTrackerProtocol.ACTION_CONNECT;
	private final int transactionId;
	
	public ConnectRequest() {
		transactionId = UdpTrackerProtocol.nextRandomTransactionId();
	}
		
	public int getTransactionId() {
		return transactionId;
	}
 
	/*
	   Offset  Size            Name            Value
			0       64-bit integer  connection_id   0x41727101980
			8       32-bit integer  action          0 // connect
			12      32-bit integer  transaction_id
			16

	 */
	public byte[] encode() {
		ByteBuffer buf = ByteBuffer.allocate(16);
		buf.putLong(connectionId);
		buf.putInt(action);
		buf.putInt(transactionId);
		return buf.array();
	}
}
