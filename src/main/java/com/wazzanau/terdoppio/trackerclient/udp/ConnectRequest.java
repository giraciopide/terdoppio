package com.wazzanau.terdoppio.trackerclient.udp;

import java.nio.ByteBuffer;
import java.util.Random;

/**
 * Offset  Size            Name            Value
		0       64-bit integer  connection_id   0x41727101980
		8       32-bit integer  action          0 // connect
		12      32-bit integer  transaction_id
		16

 */
public class ConnectRequest {

	private static final Random rnd = new Random();
	private final long connectionId = 0x41727101980L;
	private final int action = UdpTrackerProtocol.ACTION_CONNECT;
	private final int transactionId;
	
	public ConnectRequest(int transactionId) {
		this.transactionId = transactionId;
	}
	
	public ConnectRequest() {
		transactionId = rnd.nextInt();
	}
	
	public byte[] encode() {
		ByteBuffer buf = ByteBuffer.allocate(16);
		buf.putLong(connectionId);
		buf.putInt(action);
		buf.putInt(transactionId);
		return buf.array();
	}
}
