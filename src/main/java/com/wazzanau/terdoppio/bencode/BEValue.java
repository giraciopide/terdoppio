package com.wazzanau.terdoppio.bencode;

import java.nio.ByteBuffer;

public interface BEValue {
	
	BEType getType();

	/**
	 * Returns a newly allocated byte array containing the encoded BEString
	 * @return
	 */
	byte[] encode();

	/**
	 * Encodes the BEString onto the given buffer
	 * @param buf
	 */
	void encode(ByteBuffer buf);

	/**
	 * Return the length of the encoded data representation in bytes.
	 * @return
	 */
	int getEncodedLength();
	
	//
	// Interface method for type cohercion.
	//
	
	public BEString asString();
	public BEDictionary asDict();
	public BEList asList();
	public BEInteger asInt();
}