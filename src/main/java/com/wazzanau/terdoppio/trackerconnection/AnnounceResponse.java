package com.wazzanau.terdoppio.trackerconnection;

import java.util.List;

public interface AnnounceResponse {

	public long getInterval();

	public List<Peer> getPeers();

}