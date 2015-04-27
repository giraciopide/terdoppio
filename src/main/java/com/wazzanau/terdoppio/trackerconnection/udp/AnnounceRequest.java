package com.wazzanau.terdoppio.trackerconnection.udp;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class AnnounceRequest {
	private final long connectionId;
	private final int action = UdpTrackerProtocol.ACTION_ANNOUNCE;
	private final int transactionId = UdpTrackerProtocol.nextRandomTransactionId();
	private final String infoHash;
	private final String peerId;
	private final long donwloaded;
	private final long left;
	private final long uploaded;
	private final Event event;
	private final String ipAddress;
	private final byte[] rawIpAddress;
	private final int key;
	private final int numWant;
	private final int port; 
	
	public AnnounceRequest(long connectionId, String infoHash, String peerId,
			long donwloaded, long left, long uploaded, Event event, String ipAddress, int key, int numWant, int port) {
		this.connectionId = connectionId;
		
		int infoHashLen = infoHash.getBytes().length;
		if (infoHashLen != UdpTrackerProtocol.INFO_HASH_LEN) {
			throw new IllegalArgumentException("Info hash string should be " + UdpTrackerProtocol.INFO_HASH_LEN + " bytes long, was instenad: " + infoHashLen);
		}
		this.infoHash = infoHash;
		
		int peerIdLen = peerId.getBytes().length;
		if (peerIdLen != UdpTrackerProtocol.PEER_ID_LEN) {
			throw new IllegalArgumentException("Peer id string should be " + UdpTrackerProtocol.PEER_ID_LEN + " bytes long, was instenad: " + peerIdLen);
		}
		this.peerId = peerId;
		
		this.donwloaded = donwloaded;
		this.left = left;
		this.uploaded = uploaded;
		this.event = event;
		this.ipAddress = ipAddress;
		try {
			this.rawIpAddress = Inet4Address.getByName(ipAddress).getAddress();
		} catch (UnknownHostException e) {
			throw new IllegalArgumentException(e);
		}
		this.key = key;
		this.numWant = numWant;
		this.port = port;
	}

	/*
	Offset  Size    Name    Value
		0       64-bit integer  connection_id
		8       32-bit integer  action          1 // announce
		12      32-bit integer  transaction_id
		16      20-byte string  info_hash
		36      20-byte string  peer_id
		56      64-bit integer  downloaded
		64      64-bit integer  left
		72      64-bit integer  uploaded
		80      32-bit integer  event           0 // 0: none; 1: completed; 2: started; 3: stopped
		84      32-bit integer  IP address      0 // default
		88      32-bit integer  key
		92      32-bit integer  num_want        -1 // default
		96      16-bit integer  port
		98
	*/
	public byte[] encode() {
		ByteBuffer buf = ByteBuffer.allocate(98); // the len of the request packet.
		buf.putLong(connectionId);
		buf.putInt(action);
		buf.putInt(transactionId);
		buf.put(infoHash.getBytes());
		buf.put(peerId.getBytes());
		buf.putLong(donwloaded);
		buf.putLong(left);
		buf.putLong(uploaded);
		buf.putInt(event.getValue());
		buf.put(rawIpAddress);
		buf.putInt(key);
		buf.putInt(numWant);
		buf.putShort((short) port);
		return buf.array();
	}

	public long getConnectionId() {
		return connectionId;
	}

	public int getAction() {
		return action;
	}

	public int getTransactionId() {
		return transactionId;
	}

	public String getInfoHash() {
		return infoHash;
	}

	public String getPeerId() {
		return peerId;
	}

	public long getDonwloaded() {
		return donwloaded;
	}

	public long getLeft() {
		return left;
	}

	public long getUploaded() {
		return uploaded;
	}

	public Event getEvent() {
		return event;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public int getKey() {
		return key;
	}

	public int getNumWant() {
		return numWant;
	}

	public int getPort() {
		return port;
	}

	@Override
	public String toString() {
		return "AnnounceRequest [connectionId=" + connectionId + ", action=" + action + ", transactionId="
				+ transactionId + ", infoHash=" + infoHash + ", peerId=" + peerId + ", donwloaded=" + donwloaded
				+ ", left=" + left + ", uploaded=" + uploaded + ", event=" + event + ", ipAddress=" + ipAddress
				+ ", rawIpAddress=" + Arrays.toString(rawIpAddress) + ", key=" + key + ", numWant=" + numWant
				+ ", port=" + port + "]";
	}
}
