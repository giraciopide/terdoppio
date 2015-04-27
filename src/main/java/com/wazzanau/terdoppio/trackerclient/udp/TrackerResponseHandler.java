package com.wazzanau.terdoppio.trackerclient.udp;

public interface TrackerResponseHandler<R> {
	public void onResponse(R response);
}
