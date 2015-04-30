package com.wazzanau.terdoppio.trackerconnection;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.wazzanau.terdoppio.trackerconnection.udp.ScrapeResponse;

public abstract class TrackerConnection {
	
	private final String peerId;
	private final byte[] infoHash;
	private long uploaded = 0;
	private long downloaded = 0;
	private long left = -1;
	private final byte[] ipAddress;
	private final int port;
	private volatile int numWant = 50;
	private volatile String key = null;
	private final List<TrackerConnectionListener> listeners = new CopyOnWriteArrayList<>();
	
	public abstract void start();
	public abstract void stop();
	
	public void addTrackerConnectionListener(TrackerConnectionListener listener) {
		listeners.add(listener);
	}
	
	public boolean removeTrackerConnectionListener(TrackerConnectionListener listener) {
		return listeners.remove(listener);
	}
	
	protected void notifyListeners(AnnounceResponse response) {
		for (TrackerConnectionListener listener: listeners) {
			try {
				listener.onAnnounceResponse(response);
			} catch (Exception e) {
				e.printStackTrace();
				// TODO log properly here
			}
		}
	}
	
	protected void notifyListeners(ScrapeResponse response) {
		for (TrackerConnectionListener listener: listeners) {
			try {
				listener.onScrapeResponse(response);
			} catch (Exception e) {
				e.printStackTrace();
				// TODO log properly here
			}
		}
	}
	
	protected void notifyListeners(Exception exception) {
		for (TrackerConnectionListener listener: listeners) {
			try {
				listener.onTrackerConnectionException(exception);
			} catch (Exception e) {
				e.printStackTrace();
				// TODO log properly here
			}
		}
	}
	
	public TrackerConnection(String peerId, byte[] infoHash, byte[] ipAddress, int port) {
		this.peerId = peerId;
		this.infoHash = infoHash;
		this.ipAddress = ipAddress;
		this.port = port;
	}
	
	public String getPeerId() {
		return peerId;
	}

	public byte[] getInfoHash() {
		return infoHash;
	}
	
	public synchronized void setCounters(long uploaded, long downloaded, long left) {
		this.uploaded = uploaded;
		this.downloaded = downloaded;
		this.left = left;
	}

	public synchronized long getUploaded() {
		return uploaded;
	}
	
	public synchronized long getDownloaded() {
		return downloaded;
	}
	
	public synchronized long getLeft() {
		return left;
	}
	
	public byte[] getIpAddress() {
		return ipAddress;
	}
	
	public int getPort() {
		return port;
	}
	
	public int getNumWant() {
		return numWant;
	}
	
	public void setNumWant(int numWant) {
		this.numWant = numWant;
	}
	
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
}
