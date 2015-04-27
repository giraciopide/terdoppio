package com.wazzanau.terdoppio.trackerclient.udp;

import java.util.Random;

public final class UdpTrackerProtocol {
	public static int ACTION_CONNECT = 0;
	public static int ACTION_ANNOUNCE = 1;
	public static int ACTION_SCRAPE = 2;
	public static int ACTION_ERROR = 3;
	
	public static int EVENT_NONE = 0;
	public static int EVENT_COMPLETED = 1;
	public static int EVENT_STARTED = 2;
	public static int EVENT_STOPPED = 3;
	
	public static int MIN_CONNECT_RESPONSE_LEN = 16;
	public static int MIN_SCRAPE_RESPONSE_LEN = 8;
	public static int MIN_ANNOUNCE_RESPONSE_LEN = 20;
	
	public static int INFO_HASH_LEN = 20;
	public static int PEER_ID_LEN = 20;
	
	public static long CONNECT_REQUEST_CONNECTION_ID = 0x41727101980L; // magic number as per protocol spec.
	
	private final static Random rnd = new Random();
	
	public static int nextRandomTransactionId() {
		return rnd.nextInt();
	}
}
