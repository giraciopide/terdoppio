package com.wazzanau.terdoppio.trackerconnection.udp;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScrapeRequest {
	private final long connectionId;
	private final int action = UdpTrackerProtocol.ACTION_SCRAPE;
	private final int transactionId = UdpTrackerProtocol.nextRandomTransactionId();
	private final List<String> infoHashes = new ArrayList<String>();
	
	public ScrapeRequest(long connectionId) {
		this.connectionId = connectionId;
	}

	/*
		Offset          Size            Name            Value
		0               64-bit integer  connection_id
		8               32-bit integer  action          2 // scrape
		12              32-bit integer  transaction_id
		16 + 20 * n     20-byte string  info_hash
		16 + 20 * N
	*/
	public byte[] encode() {
		ByteBuffer buf = ByteBuffer.allocate(8 + 4 + 4 + 20 * infoHashes.size()); // the len of the request packet.
		buf.putLong(connectionId);
		buf.putInt(action);
		buf.putInt(transactionId);
		for (String infoHash: infoHashes) {
			buf.put(infoHash.getBytes());
		}
		return buf.array();
	}

	public long getConnectionId() {
		return connectionId;
	}

	public int getTransactionId() {
		return transactionId;
	}

	public List<String> getInfoHashes() {
		return Collections.unmodifiableList(infoHashes);
	}
	
	public void addInfoHash(String infoHash) {
		int infoHashLen = infoHash.getBytes().length;
		if (infoHashLen != UdpTrackerProtocol.INFO_HASH_LEN) {
			throw new IllegalArgumentException("Info hash string should be " + UdpTrackerProtocol.INFO_HASH_LEN + " bytes long, was instead: " + infoHashLen);
		}
		infoHashes.add(infoHash);
	}
	
	public void addInfoHash(byte[] infoHash) {
		if (infoHash.length != UdpTrackerProtocol.INFO_HASH_LEN) {
			throw new IllegalArgumentException("Info hash string should be " + UdpTrackerProtocol.INFO_HASH_LEN + " bytes long, was instead: " + infoHash.length);
		}
		infoHashes.add(new String(infoHash));
	}
	
}
