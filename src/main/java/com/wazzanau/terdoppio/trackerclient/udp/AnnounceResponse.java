package com.wazzanau.terdoppio.trackerclient.udp;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.wazzanau.terdoppio.bencode.DecodingException;

public class AnnounceResponse {

	private final int transactionId;
	private final int interval;
	private final int leechers;
	private final int seeders;
	private final List<Peer> peers;
	
	private AnnounceResponse(int transactionId, int interval, int leechers, int seeders, List<Peer> peers) {
		this.transactionId = transactionId;
		this.interval = interval;
		this.leechers = leechers;
		this.seeders = seeders;
		this.peers = peers == null ? new ArrayList<Peer>() : peers;
	}
	
	/*
	   Offset      		Size            Name            Value
			0           32-bit integer  action          1 // announce
			4           32-bit integer  transaction_id
			8           32-bit integer  interval
			12          32-bit integer  leechers
			16          32-bit integer  seeders
			20 + 6 * n  32-bit integer  IP address
			24 + 6 * n  16-bit integer  TCP port
			20 + 6 * N
	 */
	public static AnnounceResponse decode(ByteBuffer buf) throws DecodingException {
		if (buf.remaining() < UdpTrackerProtocol.MIN_ANNOUNCE_RESPONSE_LEN) {
			throw new DecodingException("Announce response message should be at least " + UdpTrackerProtocol.MIN_CONNECT_RESPONSE_LEN + " bytes long");
		}
		
		// action should be ANNOUNCE (1)
		int action = buf.getInt();
		if (action != UdpTrackerProtocol.ACTION_ANNOUNCE) {
			throw new DecodingException("Expected action ANNOUNCE (" + Integer.toHexString(UdpTrackerProtocol.ACTION_ANNOUNCE) + ") but got: (" + Integer.toHexString(action) + ")");
		}
		
		int transactionId = buf.getInt();
		int interval = buf.getInt();
		int leechers = buf.getInt();
		int seeders = buf.getInt();
		
		List<Peer> peers = new LinkedList<Peer>();
		try {
			while (buf.hasRemaining()) {
				byte[] ip = new byte[4];
				buf.get(ip);
				int port = Short.toUnsignedInt(buf.getShort());
				Peer peer = null;
				try {
					peer = new Peer(ip, port);
				} catch (InvalidPeerException e) {
					// ignore this peer. 
				}
				peers.add(peer);
			}
		} catch (BufferUnderflowException e) {
			throw new DecodingException(e);
		}
			
		return new AnnounceResponse(transactionId, interval, leechers, seeders, peers);
	}

	public int getTransactionId() {
		return transactionId;
	}

	public int getInterval() {
		return interval;
	}

	public int getLeechers() {
		return leechers;
	}

	public int getSeeders() {
		return seeders;
	}

	public List<Peer> getPeers() {
		return Collections.unmodifiableList(peers);
	}

	@Override
	public String toString() {
		return "AnnounceResponse [transactionId=" + transactionId + ", interval=" + interval + ", leechers=" + leechers
				+ ", seeders=" + seeders + ", peers=" + peers + "]";
	}
	
	
}
