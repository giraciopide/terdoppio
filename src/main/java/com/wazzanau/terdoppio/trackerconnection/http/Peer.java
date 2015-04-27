package com.wazzanau.terdoppio.trackerconnection.http;

import com.wazzanau.terdoppio.bencode.BEDictionary;
import com.wazzanau.terdoppio.bencode.BEValue;
import com.wazzanau.terdoppio.bencode.DecodingException;
import com.wazzanau.terdoppio.trackerconnection.http.ByteUtils.Endianess;

//peers: (dictionary model) The value is a list of dictionaries, each with the following keys:
	//    peer id: peer's self-selected ID, as described above for the tracker request (string)
	//    ip: peer's IP address either IPv6 (hexed) or IPv4 (dotted quad) or DNS name (string)
	//    port: peer's port number (integer)
	// peers: (binary model) Instead of using the dictionary model described above, the peers value may be a string consisting of multiples of 6 bytes. First 4 bytes are the IP address and last 2 bytes are the port number. All in network (big endian) notation.
	
public class Peer {
	
	private static final String KEY_PEER_ID = "peer id";
	private static final String KEY_IP = "ip";
	private static final String KEY_PORT = "port";
	
	private final String peerId;
	private final String ip;
	private final int port;
	
	public Peer(String peerId, String ip, int port) {
		super();
		this.peerId = peerId;
		this.ip = ip;
		this.port = port;
	}

	public String getPeerId() {
		return peerId;
	}

	public String getIp() {
		return ip;
	}

	public long getPort() {
		return port;
	}
	
	public static Peer fromBytes(byte[] bytes, int offset) throws DecodingException {
		if (bytes.length == 6) {
			throw new DecodingException("The compact peer list is a not a multiple of 6 (4 bytes for ipv4 address, 2 bytes for port) cannot decode");
		}
		
		// first 4 bytes are ipv4 as a string... the uint32 representing the ip is in network byte order (i.e. big endian).
		long ipv4 = ByteUtils.readUInt32(bytes, offset, Endianess.BIG_ENDIAN);
		String ip = ByteUtils.readIPv4fromUInt32(ipv4);
		
		// byte 5 and 6 are an UInt16 that represents the port
		int port = ByteUtils.readUInt16(bytes, offset + 4, Endianess.BIG_ENDIAN);
		
		// sadly in compact form, there's no peer id.
		return new Peer(ip + ":" + port, ip, port);
	}
	
	/**
	 * This is to be used when deconding from non compact mode, i.e. dictionary model
	 * @param dict
	 * @return
	 * @throws DecodingException
	 */
	public static Peer fromDict(BEDictionary dict) throws DecodingException {
		BEValue ipValue = dict.get(KEY_IP);
		if (ipValue == null) {
			throw new DecodingException("Cannot decode: Missing key [" + KEY_IP + "]");
		}

		BEValue portValue = dict.get(KEY_PORT);
		if (portValue == null) {
			throw new DecodingException("Cannot decode: Missing key [" + KEY_PORT + "]");
		}
		
		// Peer ID is optional
		BEValue peerIdValue = dict.get(KEY_PEER_ID);
		String peerId = peerIdValue != null ? dict.get(KEY_PEER_ID).asString().get() : null;
		
		String ip = ipValue.asString().get();
		
		// we know port range fits into an int so the cast is safe.
		int port = (int) portValue.asInt().get();
		return new Peer(peerId, ip, port);
	}

	@Override
	public String toString() {
		return "Peer [peerId=" + peerId + ", ip=" + ip + ", port=" + port + "]";
	}
}
