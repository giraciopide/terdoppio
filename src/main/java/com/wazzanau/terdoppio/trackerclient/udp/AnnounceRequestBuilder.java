package com.wazzanau.terdoppio.trackerclient.udp;

public class AnnounceRequestBuilder {
	private long connectionId;
	private String infoHash;
	private String peerId;
	private long donwloaded;
	private long left;
	private long uploaded;
	private Event event;
	private String ipAddress;
	private int key;
	private int numWant = -1;
	private short port; 
	
	public AnnounceRequest build() {
		return new AnnounceRequest(connectionId,
									infoHash,
									peerId,
									donwloaded,
									left,
									uploaded,
									event,
									ipAddress,
									key,
									numWant,
									port);
	}
	
	public void setConnectionId(long connectionId) {
		this.connectionId = connectionId;
	}

	public void setInfoHash(String infoHash) {
		this.infoHash = infoHash;
	}

	public void setPeerId(String peerId) {
		this.peerId = peerId;
	}

	public void setDonwloaded(long donwloaded) {
		this.donwloaded = donwloaded;
	}

	public void setLeft(long left) {
		this.left = left;
	}

	public void setUploaded(long uploaded) {
		this.uploaded = uploaded;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public void setNumWant(int numWant) {
		this.numWant = numWant;
	}

	public void setPort(short port) {
		this.port = port;
	}
}