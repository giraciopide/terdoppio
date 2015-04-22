package com.wazzanau.terdoppio.trackerclient.udp;

public final class UdpTrackerProtocol {
	public static int ACTION_CONNECT = 0;
	public static int ACTION_ANNOUNCE = 1;
	public static int ACTION_SCRAPE = 2;
	public static int ACTION_ERROR = 3;
	
	public static int EVENT_NONE = 0;
	public static int EVENT_COMPLETED = 1;
	public static int EVENT_STARTED = 2;
	public static int EVENT_STOPPED = 3;
}
