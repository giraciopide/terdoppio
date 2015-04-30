package com.wazzanau.terdoppio.trackerconnection;

import com.wazzanau.terdoppio.trackerconnection.udp.ScrapeResponse;

public interface TrackerConnectionListener {
	public abstract void onAnnounceResponse(AnnounceResponse response);
	public abstract void onScrapeResponse(ScrapeResponse response);
	public abstract void onTrackerConnectionException(Exception e);
}
