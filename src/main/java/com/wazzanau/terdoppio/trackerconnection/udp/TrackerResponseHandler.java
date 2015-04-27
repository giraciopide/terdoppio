package com.wazzanau.terdoppio.trackerconnection.udp;

public interface TrackerResponseHandler<R> {
	public void onResponse(R response);
}
