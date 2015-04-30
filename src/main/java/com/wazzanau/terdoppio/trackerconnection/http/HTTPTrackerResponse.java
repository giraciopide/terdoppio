package com.wazzanau.terdoppio.trackerconnection.http;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.wazzanau.terdoppio.bencode.BEDictionary;
import com.wazzanau.terdoppio.bencode.BEValue;
import com.wazzanau.terdoppio.bencode.DecodingException;
import com.wazzanau.terdoppio.trackerconnection.AnnounceResponse;
import com.wazzanau.terdoppio.trackerconnection.Peer;

public class HTTPTrackerResponse implements AnnounceResponse {

	// failure reason: If present, then no other keys may be present. The value is a human-readable error message as to why the request failed (string).
	// warning message: (new, optional) Similar to failure reason, but the response still gets processed normally. The warning message is shown just like an error.
	// interval: Interval in seconds that the client should wait between sending regular requests to the tracker
	// min interval: (optional) Minimum announce interval. If present clients must not reannounce more frequently than this.
	// tracker id: A string that the client should send back on its next announcements. If absent and a previous announce sent a tracker id, do not discard the old value; keep using it.
	// complete: number of peers with the entire file, i.e. seeders (integer)
	// incomplete: number of non-seeder peers, aka "leechers" (integer)
	// peers: (dictionary model) The value is a list of dictionaries, each with the following keys:
	//    peer id: peer's self-selected ID, as described above for the tracker request (string)
	//    ip: peer's IP address either IPv6 (hexed) or IPv4 (dotted quad) or DNS name (string)
	//    port: peer's port number (integer)
	// peers: (binary model) Instead of using the dictionary model described above, the peers value may be a string consisting of multiples of 6 bytes. First 4 bytes are the IP address and last 2 bytes are the port number. All in network (big endian) notation.

	private static final String KEY_FAILURE_REASON = "failure reason";
	private static final String KEY_WARNING_MESSAGE = "warning message";
	private static final String KEY_INTERVAL = "interval";
	private static final String KEY_MIN_INTERVAL = "min interval";
	private static final String KEY_TRACKER_ID = "tracker id";
	private static final String KEY_COMPLETE = "complete";
	private static final String KEY_INCOMPLETE = "incomplete";
	private static final String KEY_PEERS = "peers";

	private String failureReason;
	private String warningMessage;
	private long interval;
	private long minInterval;
	private String trackerId;
	private long complete;
	private long incomplete;
	private List<Peer> peers = new ArrayList<Peer>(50);

	private HTTPTrackerResponse() { }

	public static HTTPTrackerResponse fromDict(BEDictionary dict) throws DecodingException {
		HTTPTrackerResponse response = new HTTPTrackerResponse();

		BEValue failureReason = dict.get(KEY_FAILURE_REASON);
		if (failureReason != null) {
			response.failureReason = failureReason.asString().get();
		}

		BEValue warningMessage = dict.get(KEY_WARNING_MESSAGE);
		if (warningMessage != null) {
			response.warningMessage = warningMessage.asString().get();
		}

		BEValue interval = dict.get(KEY_INTERVAL);
		if (interval == null) {
			throw new DecodingException("Missing required key: [" + KEY_INTERVAL + "] in tracker response");
		}
		response.interval = interval.asInt().get();

		BEValue minInterval = dict.get(KEY_MIN_INTERVAL);
		if (minInterval != null) {
			response.minInterval = minInterval.asInt().get();
		}

		BEValue trackerId = dict.get(KEY_TRACKER_ID);
		if (trackerId != null) {
			response.trackerId = trackerId.asString().get();
		}

		BEValue complete = dict.get(KEY_COMPLETE);
		if (complete != null) {
			response.complete = trackerId.asInt().get();
		}

		BEValue incomplete = dict.get(KEY_INCOMPLETE);
		if (incomplete != null) {
			response.incomplete = incomplete.asInt().get();
		}

		BEValue peersValue = dict.get(KEY_PEERS);
		List<Peer> peers = new ArrayList<Peer>();
		switch (peersValue.getType()) {
		case LIST: // list of dictionaries
			for (BEValue item: peersValue.asList()) {
				peers.add(HTTPPeer.fromDict(item.asDict()));
			}
			break;
		case STRING: // multiple of 6 bytes string
			byte[] peersBytes = peersValue.asString().getRawBytes();
			if ((peersBytes.length % 6) != 0) {
				throw new DecodingException("Unexpected length in peers string, not multiple of 6, was " + peersBytes.length + " bytes long");
			}
			
			// 6 bytes, 4 for ipv4 (UInt32), 2 for port (UInt16).
			for (int offset = 0; offset < peersBytes.length; offset += 6) {
				peers.add(HTTPPeer.fromBytes(peersBytes, offset));
			}
			break;
		case DICTIONARY:
		case INTEGER:
		default:
			throw new DecodingException("Unexpected type for key [" + KEY_PEERS + "] should be dictionary or string, was instead: " + peersValue.getType());
		}
		response.peers = peers;

		return response;
	}

	public String getFailureReason() {
		return failureReason;
	}

	public String getWarningMessage() {
		return warningMessage;
	}

	@Override
	public long getInterval() {
		return interval;
	}
	
	public long getMinInterval() {
		return minInterval;
	}

	public String getTrackerId() {
		return trackerId;
	}

	public long getComplete() {
		return complete;
	}

	public long getIncomplete() {
		return incomplete;
	}

	@Override
	public List<Peer> getPeers() {
		return Collections.unmodifiableList(peers);
	}

}
