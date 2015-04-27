package com.wazzanau.terdoppio.bencode;

import java.nio.ByteBuffer;

/**
 * Integers are represented by an 'i' followed by the number in base 10 followed by an 'e'. 
 * For example i3e corresponds to 3 and i-3e corresponds to -3. Integers have no size 
 * limitation. i-0e is invalid. 
 * All encodings with a leading zero, such as i03e, are invalid, other than i0e, 
 * which of course corresponds to 0.
 */
public class BEInteger implements BEValue {

	private final long value;
	
	public BEInteger(long value) {
		this.value = value;
	}

	public long get() {
		return value;
	}
	
	@Override
	public byte[] encode() {
		ByteBuffer buf = ByteBuffer.allocate(getEncodedLength()); 
		encode(buf);
		return buf.array();
	}

	@Override
	public void encode(ByteBuffer buf) {
		buf.put(BEncoding.DELIM_INT_START);
		buf.put(Long.toString(value).getBytes());
		buf.put(BEncoding.DELIM_INT_END);
	}

	@Override
	public int getEncodedLength() {
		return Long.toString(value).getBytes().length + 2; // 1 for i and 1 for e
	}
	
	@Override
	public BEType getType() {
		return BEType.INTEGER;
	}
	
	//
	// Value coercion interface
	// 
	
	@Override
	public BEString asString() {
		throw new ClassCastException("Cannot bring a BEInteger to a BEString");
	}

	@Override
	public BEDictionary asDict() {
		throw new ClassCastException("Cannot bring a BEInteger to a BEDictionary");
	}

	@Override
	public BEList asList() {
		throw new ClassCastException("Cannot bring a BEInteger to a BEList");
	}

	@Override
	public BEInteger asInt() {
		return this;
	}
	
	@Override
	public String toString() {
		return Long.toString(value);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (value ^ (value >>> 32));
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
		BEInteger other = (BEInteger) obj;
		if (value != other.value)
			return false;
		return true;
	}
	
	
}
