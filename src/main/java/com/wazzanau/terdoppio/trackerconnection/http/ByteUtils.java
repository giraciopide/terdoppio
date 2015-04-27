package com.wazzanau.terdoppio.trackerconnection.http;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ByteUtils {
		
	public static enum Endianess {
		BIG_ENDIAN,
		LITTLE_ENDIAN
	}
	
	/**
	 * Converts a java byte (signed 8 bit) to an int containing the unsigned byte representation 
	 * @param b
	 * @return an int where the value of the given signed byte is interpreted as unsigned byte
	 */
	public static int toUint8(byte b) {
		return (int)b & 0xFF;
	}
	
	/**
	 * Converts a java short (16 bit) (which is always signed) to an int with containing the unsigned short representation
	 * @param signedShort
	 * @return an int where the value of the given signed short is interpreted as unsigned short
	 */
	public static int toUInt16(short signedShort) {
		return signedShort & 0x0000ffff;
	}
	
	/**
	 * Converts a java int (signed 32 bit) to a long containing the unsigned int representation 
	 * @param signedInt
	 * @return an int where the value of the given signed short is interpreted as unsigned short
	 */
	public static long toUInt32(int signedInt) {
		return signedInt & 0x00000000ffffffffL;
	}
	
	/**
	 * Reads an UInt16 from a byte array into a java int.
	 * @param num
	 * @param offset
	 * @param endianess
	 * @return
	 */
	public static int readUInt16(byte[] num, int offset, Endianess endianess) {
		int out = 0;
		switch (endianess) {
		case BIG_ENDIAN:
			out = (int)num[offset + 0] & 0xFF;
			out += ((int)num[offset + 1] & 0xFF) << 8;
			break;
		case LITTLE_ENDIAN:
			out = (int)num[offset + 1] & 0xFF;
			out += ((int)num[offset + 0] & 0xFF) << 8;
			break;
		default:
			// won't happen
			break;
		}
		return out;
	}
	
	public static int readUInt16(byte[] num, Endianess endianess) {
		return readUInt16(num, 0, endianess);
	}
	
	public static long readUInt32(byte[] num, Endianess endianess) {
		return readUInt32(num, 0, endianess);
	}
	
	/**
	 * Reads an UInt32 from a byte array into a java long.
	 * @param num
	 * @param offset
	 * @param endianess
	 * @return
	 */
	public static long readUInt32(byte[] num, int offset, Endianess endianess) {
		long out = 0;
		switch (endianess) {
		case BIG_ENDIAN:
			out = (long)num[offset + 0] & 0xFF;
			out += ((long)num[offset + 1] & 0xFF) << 8;
			out += ((long)num[offset + 2] & 0xFF) << 16;
			out += ((long)num[offset + 3] & 0xFF) << 24;
			break;
		case LITTLE_ENDIAN:
			out = (long)num[offset + 3] & 0xFF;
			out += ((long)num[offset + 2] & 0xFF) << 8;
			out += ((long)num[offset + 1] & 0xFF) << 16;
			out += ((long)num[offset + 0] & 0xFF) << 24;
			break;
		default:
			// won't happen
			break;
		}
		return out;
	}
	
	public static String readIPv4fromUInt32(long uInt32Ipv4) {
		uInt32Ipv4 = uInt32Ipv4 & 0x00000000FFFFFFFF;
		StringBuilder ipv4 = new StringBuilder();
		ipv4.append((uInt32Ipv4 & 0xFF000000) >> 24).append(".");
		ipv4.append((uInt32Ipv4 & 0x00FF0000) >> 16).append(".");
		ipv4.append((uInt32Ipv4 & 0x0000FF00) >> 8).append(".");
		ipv4.append((uInt32Ipv4 & 0x000000FF) >> 0);
		return ipv4.toString();
	}
	
	public static MessageDigest getSHA1Digest() {
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			// will not happen.
		}
		return digest;
	}
	
	public static byte[] computeSHA1(byte[] bytes) {
		return getSHA1Digest().digest(bytes);
	}
	
	public static String toString(byte[] bytes) {
		StringBuilder out = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			out.append((char)bytes[i]);
		}
		return out.toString();
	}
}
