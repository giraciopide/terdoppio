package com.wazzanau.bencoding;

import java.nio.ByteBuffer;

import org.junit.Assert;
import org.junit.Test;

import com.wazzanau.terdoppio.bencode.BEString;
import com.wazzanau.terdoppio.bencode.BEncoding;
import com.wazzanau.terdoppio.bencode.DecodingException;

public class BEStringTest {

	@Test
	public void testDecodeEncodeFromByteBuffer() throws DecodingException {
		String str = "25:this is a bencoded string";
		String dataStr = str.substring(3);
		
		// decoding
		ByteBuffer buf = ByteBuffer.allocate(str.getBytes().length);
		buf.put(str.getBytes());
		buf.rewind();

		BEString s = BEncoding.decodeBEString(buf);
		Assert.assertEquals(28, s.getEncodedLength());
		Assert.assertEquals(dataStr, s.get());

		// encoding
		ByteBuffer outBuf = ByteBuffer.allocate(str.getBytes().length);
		s.encode(outBuf);
		Assert.assertEquals(28, outBuf.position());
		Assert.assertArrayEquals(str.getBytes(), outBuf.array());
	}
}
