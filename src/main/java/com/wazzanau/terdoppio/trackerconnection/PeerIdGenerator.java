package com.wazzanau.terdoppio.trackerconnection;

import java.nio.ByteBuffer;
import java.util.Random;

public final class PeerIdGenerator {
	
	private final static Random rnd = new Random();
	public final static int idLen = 20;
	public final static byte[] idPrefix = "-TDP".getBytes(); // the constant part for Terdoppio

	private PeerIdGenerator() { }
	
	/**
	 * Generates a new peer id. The peer id generated will be in the form: 
     * the string "-TDP" followed by 16 random bytes. Note that the the random part might will not necessarily containing
     * only printable ascii characters.
	 */
	public static byte[] generatePeerId() {
		ByteBuffer id = ByteBuffer.allocate(idLen);
		id.put(idPrefix);
		byte[] randomPart = new byte[idLen - idPrefix.length];
		rnd.nextBytes(randomPart);
		id.put(randomPart);
		return id.array();
	}
	
	/**
	 * Generates a new peer id. The peer id generated will be in the form: 
     * the string "-TDP" followed by 16 random ascii printable chars.
	 */
	public static byte[] generateReadablePeerId() {
		ByteBuffer id = ByteBuffer.allocate(idLen);
		id.put(idPrefix);
		for (int i = 0; i < idLen - idPrefix.length; ++i) {
			id.put(nextPrintableChar());
		}
		return id.array();
	}
	
	public static String generateReadablePeerIdString() {
		return new String(generateReadablePeerId());
	}
	
	public static String generatePeerIdString() {
		return new String(generatePeerId());
	}
	
	private static byte nextPrintableChar() {
		return (byte)(rnd.nextInt(126 - 32) + 32); // all the range of printables ascii chars
	}
}
