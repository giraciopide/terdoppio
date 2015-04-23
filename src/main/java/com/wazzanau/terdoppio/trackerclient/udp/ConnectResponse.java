package com.wazzanau.terdoppio.trackerclient.udp;

import java.nio.ByteBuffer;

import com.wazzanau.terdoppio.bencode.DecodingException;



/**
 * Represents the udp tracker response.
 */
public class ConnectResponse {

	private final int transactionId;
	private final long connectionId;
	
	private ConnectResponse(int transactionId, long connectionId) {
		this.transactionId = transactionId;
		this.connectionId = connectionId;
	}
	
	public int getTransactionId() {
		return transactionId;
	}

	public long getConnectionId() {
		return connectionId;
	}

	/*
	Offset  Size            Name            Value
		0       32-bit integer  action          0 // connect
		4       32-bit integer  transaction_id
		8       64-bit integer  connection_id
		16
	*/
	public static ConnectResponse decode(ByteBuffer buf) throws DecodingException {
		if (buf.remaining() < UdpTrackerProtocol.MIN_CONNECT_RESPONSE_LEN) {
			throw new DecodingException("Connect response message should be at least " + UdpTrackerProtocol.MIN_CONNECT_RESPONSE_LEN + " bytes long");
		}
		
		// getting the action
		int action = buf.getInt();
		if (action != UdpTrackerProtocol.ACTION_CONNECT) {
			throw new DecodingException("Expected action CONNECT (" + Integer.toHexString(UdpTrackerProtocol.ACTION_CONNECT) + ") but got: (" + Integer.toHexString(action) + ")");
		}
		
		// getting transaction id and connection id
		int transactionId = buf.getInt();
		long connectionId = buf.getLong();
		
		return new ConnectResponse(transactionId, connectionId);
	}
}
