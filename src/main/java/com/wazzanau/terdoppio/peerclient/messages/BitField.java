package com.wazzanau.terdoppio.peerclient.messages;

public class BitField extends PeerMessage {
	
	private final byte[] bitField;

	public BitField(byte[] bitField) {
		super(PeerMessageType.BITFIELD);
		this.bitField = bitField;
	}

	public byte[] getBitField() {
		return bitField;
	}
}
