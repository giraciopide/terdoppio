package com.wazzanau.terdoppio.bencode;

import com.wazzanau.terdoppio.ByteUtils;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Comparator;

/**
 * Strings are length-prefixed base ten followed by a colon and the string. For example 4:spam corresponds to 'spam'.
 * Strings are not necessarily 
 * @author marco
 *
 */
public class BEString implements BEValue {

	public final static Comparator<BEString> RAW_BYTES_COMPARATOR = (as, bs) -> {
		final byte[] a = as.getRawBytes();
		final byte[] b = bs.getRawBytes();

		final int len = Math.min(a.length, b.length);

		for (int i = 0; i < len; ++i) {
			int comparison = ByteUtils.compareUnsigned(a[i], b[i]);
			if (comparison != 0) {
				return comparison;
			}
		}

		if (a.length > b.length) {
			return 1;
		} else if (a.length < b.length) {
			return -1;
		}

		return 0;
	};

	private final static byte LEN_SEPARATOR = ':';
	private final String str;
	private final byte[] data;
	private final byte[] prefixLen;

	public BEString(byte[] data) {
		this.data = data;
		this.str = new String(data);
		this.prefixLen = Integer.toString(data.length).getBytes();
	}
	
	public BEString(String str) {
		this.str = str;
		this.data = str.getBytes();
		this.prefixLen = Integer.toString(data.length).getBytes();
	}
	
	public String get() {
		return new String(data);
	}
	
	public byte[] getRawBytes() {
		return data;
	}
	
	@Override
	public int getEncodedLength() {
		return prefixLen.length + 1 + data.length; // 1 is LEN_SEPARATOR
	}

	public String getDataAsString() {
		return new String(data, Charset.forName("UTF-8"));
	}

	/**
	 * @see com.wazzanau.terdoppio.bencode.BEValue#encode()
	 */
	@Override
	public byte[] encode() {
		ByteBuffer buf = ByteBuffer.allocate(getEncodedLength()); // 1 is for LEN_SEPARATOR
		encode(buf);
		return buf.array();
	}
	
	/**
	 * @see com.wazzanau.terdoppio.bencode.BEValue#encode(java.nio.ByteBuffer)
	 */
	@Override
	public void encode(ByteBuffer buf) {
		buf.put(prefixLen);
		buf.put(LEN_SEPARATOR);
		buf.put(data);
	}

	@Override
	public BEType getType() {
		return BEType.STRING;
	}
	
	//
	// Value coercion interface
	// 
	
	@Override
	public BEString asString() {
		return this;
	}

	@Override
	public BEDictionary asDict() {
		throw new ClassCastException("Cannot bring a asBEString to a BEDictionary");
	}

	@Override
	public BEList asList() {
		throw new ClassCastException("Cannot bring a asBEString to a BEDictionary");
	}

	@Override
	public BEInteger asInt() {
		throw new ClassCastException("Cannot bring a asBEString to a BEInteger");
	}
	
	@Override
	public String toString() {
		return str;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((str == null) ? 0 : str.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BEString other = (BEString) obj;
		if (str == null) {
			if (other.str != null)
				return false;
		} else if (!str.equals(other.str))
			return false;
		return true;
	}
}
