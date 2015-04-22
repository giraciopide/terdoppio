package com.wazzanau.terdoppio.trackerclient.http;

import java.nio.ByteBuffer;
import java.util.Random;

public class PeerIdGenerator {
	
	private final static Random rnd = new Random();
	public final static int idLen = 20;
	public final static byte[] idPrefix = "-TDP".getBytes(); // the constant part for Terdoppio

	private PeerIdGenerator() { }
	
	public static byte[] generatePeerId() {
		ByteBuffer id = ByteBuffer.allocate(idLen);
		id.put(idPrefix);
		byte[] randomPart = new byte[idLen - idPrefix.length];
		rnd.nextBytes(randomPart);
		id.put(randomPart);
		return id.array();
	}
	
	public static byte nextPrintableChar() {
		return (byte)(rnd.nextInt(126 - 32) + 32); // all the range of printables ascii chars
	}
	
	public static byte[] generateReadablePeerId() {
		ByteBuffer id = ByteBuffer.allocate(idLen);
		id.put(idPrefix);
		for (int i = 0; i < idLen - idPrefix.length; ++i) {
			id.put(nextPrintableChar());
		}
		return id.array();
	}
	
	public static void main(String[] args) {
		byte[] peerId = PeerIdGenerator.generateReadablePeerId();
		System.out.println(ByteUtils.toString(peerId));
	}
}
