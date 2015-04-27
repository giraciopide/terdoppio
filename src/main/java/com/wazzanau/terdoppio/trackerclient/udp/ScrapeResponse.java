package com.wazzanau.terdoppio.trackerclient.udp;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.wazzanau.terdoppio.bencode.DecodingException;
import com.wazzanau.terdoppio.trackerclient.http.ByteUtils;
import com.wazzanau.terdoppio.trackerclient.udp.ScrapeResponse.ScrapeInfo;



/**
 * Represents the udp tracker response.
 */
public class ScrapeResponse implements Iterable<ScrapeInfo>, Response {

	private final int transactionId;
	private final List<ScrapeInfo> scrapedInfo;
	
	private ScrapeResponse(int transactionId, List<ScrapeInfo> scrapedInfo) {
		this.transactionId = transactionId;
		this.scrapedInfo = scrapedInfo;
	}
	
	@Override
	public int getTransactionId() {
		return transactionId;
	}
	
	@Override
	public Iterator<ScrapeInfo> iterator() {
		return scrapedInfo.iterator();
	}

	/*
		Offset      Size            Name            Value
		0           32-bit integer  action          2 // scrape
		4           32-bit integer  transaction_id
		8 + 12 * n  32-bit integer  seeders
		12 + 12 * n 32-bit integer  completed
		16 + 12 * n 32-bit integer  leechers
		8 + 12 * N
	*/
	public static ScrapeResponse decode(ByteBuffer buf) throws DecodingException {
		if (buf.remaining() < UdpTrackerProtocol.MIN_SCRAPE_RESPONSE_LEN) {
			throw new DecodingException("Connect response message should be at least " + UdpTrackerProtocol.MIN_SCRAPE_RESPONSE_LEN + " bytes long");
		}

		int action = buf.getInt();
		if (action != UdpTrackerProtocol.ACTION_CONNECT) {
			throw new DecodingException("Expected action CONNECT (" + Integer.toHexString(UdpTrackerProtocol.ACTION_CONNECT) + ") but got: (" + Integer.toHexString(action) + ")");
		}

		int transactionId = buf.getInt();
		
		if (buf.remaining() % 12 != 0) {
			throw new DecodingException("Expected the content part of the scrape response to be a multiple of 12 bytes long. was instead: " + buf.remaining());
		}
		
		List<ScrapeInfo> scrapedInfo = new ArrayList<ScrapeInfo>();
		while (buf.hasRemaining()) {
			long seeders = ByteUtils.toUInt32(buf.getInt());
			long completed = ByteUtils.toUInt32(buf.getInt());
			long leechers = ByteUtils.toUInt32(buf.getInt());
			scrapedInfo.add(new ScrapeInfo(seeders, completed, leechers));
		}
		
		return new ScrapeResponse(transactionId, scrapedInfo);
	}
	
	public ScrapeInfo getScrapedInfos(int pos) {
		return scrapedInfo.get(pos);
	}
	
	public static final class ScrapeInfo {
		private final long seeders;
		private final long completed;
		private final long leechers;
		
		public ScrapeInfo(long seeders, long completed, long leechers) {
			this.seeders = seeders;
			this.completed = completed;
			this.leechers = leechers;
		}

		public long getSeeders() {
			return seeders;
		}

		public long getCompleted() {
			return completed;
		}

		public long getLeechers() {
			return leechers;
		}
	}
}
