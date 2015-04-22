package com.wazzanau.terdoppio.bencode;

import java.io.IOException;
import java.io.InputStream;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import com.wazzanau.terdoppio.metainfo.Piece;
import com.wazzanau.terdoppio.metainfo.Pieces;

public class BEncoding {
	
	public final static byte DELIM_INT_START = 'i';
	public final static byte DELIM_INT_END = 'e';
	public final static byte DELIM_DICT_START = 'd';
	public final static byte DELIM_DICT_END = 'e';
	public final static byte DELIM_LIST_START = 'l';
	public final static byte DELIM_LIST_END = 'e';
	public final static byte DELIM_STRING_LEN = ':';
	public final static int PIECE_LEN = 20;
	
	/**
	 * Tries to consume the given byte from the buf.
	 * @param buf
	 * @param delim
	 * @throws DecodingException
	 */
	private static void consumeDelimiter(ByteBuffer buf, byte delim) throws DecodingException {
		byte foundDelim = buf.get();
		if (foundDelim != delim) {
			throw new DecodingException("Found unexpected delimiter: expected: [" + delim + "] found [" + foundDelim + "]");
		}
	}
	
	/**
	 * 
	 * @param buf
	 * @param delim
	 * @return
	 * @throws DecodingException
	 */
	private static String consumeAsStringUntilDelimiter(ByteBuffer buf, byte delim) throws DecodingException {
		StringBuilder out = new StringBuilder();
		
		byte b;
		do {
			try {
				b = buf.get();	
			} catch (BufferUnderflowException e) {
				throw new DecodingException("Buffer underflow while consuming up to delimiter: [" + delim + "]");
			}
			
			if (b == delim) {
				break;
			}
			out.appendCodePoint(b & 0xFF);
			
		} while (true);
		
		return out.toString();
	}
	
	/**
	 * 
	 * @param buf
	 * @return
	 * @throws DecodingException
	 */
	public static BEInteger decodeInt(ByteBuffer buf) throws DecodingException {
		consumeDelimiter(buf, DELIM_INT_START);
		String intAsString = consumeAsStringUntilDelimiter(buf, DELIM_INT_END);
		long value;
		try {
			value = Long.parseLong(intAsString);
		} catch (NumberFormatException e) {
			throw new DecodingException(e);
		}
		return new BEInteger(value);
	}
	
	/**
	 * 
	 * @param str
	 * @return
	 * @throws DecodingException
	 */
	public static int decodeASCIIInt(String str) throws DecodingException {
		int value;
		try {
			value = Integer.parseInt(str);
		} catch (NumberFormatException e) {
			throw new DecodingException(e);
		}
		return value;
	}
	
	public static BEString decodeBEString(ByteBuffer buf) throws DecodingException {
		String intStr = consumeAsStringUntilDelimiter(buf, DELIM_STRING_LEN);
		int len = BEncoding.decodeASCIIInt(intStr);
		
		byte[] str = new byte[len];
		try {
			buf.get(str, 0, len);
		} catch (BufferUnderflowException | ArrayIndexOutOfBoundsException e) {
			throw new DecodingException("Not enough data in buffer to read a string of len: [" + len + "]", e);
		}
		return new BEString(str);
	}
	
	public static BEDictionary decodeBEDictionary(ByteBuffer buf) throws DecodingException {
		consumeDelimiter(buf, DELIM_DICT_START);
		BEDictionary dict = new BEDictionary();
		
		while (peek(buf) != DELIM_DICT_END) {
			BEString key = decodeBEString(buf);
			BEValue value = consumeNextItem(buf);
			dict.put(key, value);
		}
		
		consumeDelimiter(buf, DELIM_DICT_END);
		return dict;
	}
	
	public static BEValue decodeBEList(ByteBuffer buf) throws DecodingException {
		consumeDelimiter(buf, DELIM_LIST_START);
		BEList list = new BEList();
		
		while (peek(buf) != DELIM_LIST_END) {
			BEValue item = consumeNextItem(buf);
			list.add(item);
		}
		
		consumeDelimiter(buf, DELIM_LIST_END);
		return list;
	}
	
	public static BEValue consumeNextItem(ByteBuffer buf) throws DecodingException {
		BEValue item;
		byte b = peek(buf);
		switch (b) {
		case DELIM_DICT_START:
			item = decodeBEDictionary(buf);
			break;
		case DELIM_INT_START:
			item = decodeInt(buf);
			break;
		case DELIM_LIST_START:
			item = decodeBEList(buf);
			break;
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9':
		case '0':
			item = decodeBEString(buf);
			break;
		default:
			throw new DecodingException("Unexpected start of dictionary value: [" + b + "]");
		}
		return item;
	}

	/**
	 * Reads a byte from the buf without consuming it.
	 * @param buf
	 * @return
	 * @throws DecodingException 
	 */
	private static byte peek(ByteBuffer buf) throws DecodingException {
		ByteBuffer peekBuf = buf.slice();
		byte b;
		try {
			b = peekBuf.get();
		} catch (BufferUnderflowException e) {
			throw new DecodingException("Not enough data in buffer to read", e);
		}
		
		return b;
	}
	
	//
	// Input stream version of the decoding methods
	// 
	
	/**
	 * Reads a byte from the stream without consuming it
	 * @param stream
	 * @return
	 * @throws DecodingException
	 */
	private static byte peek(InputStream stream) throws DecodingException {
		if (!stream.markSupported()) {
			throw new DecodingException("This parser need lookahead, hence a stream that supports mark() and reset()");
		}
		
		byte b;
		try {
			stream.mark(1);
			b = (byte) stream.read(); // the read return a unsigned byte into a int, we can safely cast into a byteù
			stream.reset();
		} catch (IOException e) {
			throw new DecodingException(e);
		} 
				
		return b;
	}
	
	private static void consumeDelimiter(InputStream stream, byte delim) throws DecodingException {
		int foundDelim;
		try {
			foundDelim = stream.read();
		} catch (IOException e) {
			throw new DecodingException(e);
		}
		
		// end of the stream has been reached
		if (foundDelim == -1) {
			throw new DecodingException("End of the stream reached reading for delim [" + delim + "]");
		}
		
		if (foundDelim != delim) {
			throw new DecodingException("Found unexpected delimiter: expected: [" + delim + "] found [" + foundDelim + "]");
		}
	}
	
	private static String consumeAsStringUntilDelimiter(InputStream stream, byte delim) throws DecodingException {
		StringBuilder out = new StringBuilder();
		int read;
		try {
			do {
				read = stream.read();
				if (read == delim) {
					break;
				}
			
				out.appendCodePoint(read);
			} while (true);
			
		} catch (IOException e) {
			throw new DecodingException(e);
		}
		return out.toString();
	}
	
	public static BEValue consumeNextItem(InputStream stream) throws DecodingException {
		BEValue item;
		byte b = peek(stream);
		switch (b) {
		case DELIM_DICT_START:
			item = decodeBEDictionary(stream);
			break;
			
		case DELIM_INT_START:
			item = decodeInt(stream);
			break;
			
		case DELIM_LIST_START:
			item = decodeBEList(stream);
			break;
			
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9':
		case '0':
			item = decodeBEString(stream);
			break;
			
		default:
			throw new DecodingException("Unexpected start of dictionary value: [" + b + "]");
		}
		return item;
	}

	public static BEValue decodeInt(InputStream stream) throws DecodingException {
		consumeDelimiter(stream, DELIM_INT_START);
		String intAsString = consumeAsStringUntilDelimiter(stream, DELIM_INT_END);
		long value;
		try {
			value = Long.parseLong(intAsString);
		} catch (NumberFormatException e) {
			throw new DecodingException(e);
		}
		return new BEInteger(value);
	}

	public static BEDictionary decodeBEDictionary(InputStream stream) throws DecodingException {
		consumeDelimiter(stream, DELIM_DICT_START);
		BEDictionary dict = new BEDictionary();
		
		while (peek(stream) != DELIM_DICT_END) {
			BEString key = decodeBEString(stream);
			BEValue value = consumeNextItem(stream);
			dict.put(key, value);
		}
		
		consumeDelimiter(stream, DELIM_DICT_END);
		return dict;
	}

	public static BEString decodeBEString(InputStream stream) throws DecodingException {
		String intStr = consumeAsStringUntilDelimiter(stream, DELIM_STRING_LEN);
		int len = BEncoding.decodeASCIIInt(intStr);
	
		byte[] str = new byte[len];
		try {
			stream.read(str, 0, len);
		} catch (IOException | ArrayIndexOutOfBoundsException e) {
			throw new DecodingException("Not enough data in buffer to read a string of len: [" + len + "] or IOException", e);
		}
		return new BEString(str);
	}
	
	public static BEValue decodeBEList(InputStream stream) throws DecodingException {
		consumeDelimiter(stream, DELIM_LIST_START);
		BEList list = new BEList();
		
		while (peek(stream) != DELIM_LIST_END) {
			BEValue item = consumeNextItem(stream);
			list.add(item);
		}
		
		consumeDelimiter(stream, DELIM_LIST_END);
		return list;
	}
	
	public static Pieces decodePieces(BEString pieces) throws DecodingException {
		return decodePieces(pieces.getRawBytes());
	}
	
	public static Pieces decodePieces(byte[] pieces) throws DecodingException {
		if (pieces.length % PIECE_LEN != 0) {
			throw new DecodingException("Pieces bytes should be in multiple of " + PIECE_LEN + " bytes");
		}
		
		Pieces outPieces = new Pieces();
		for (int i = 0; i < pieces.length; i += PIECE_LEN) {
			byte[] b = Arrays.copyOfRange(pieces, i, i + PIECE_LEN);
			Piece piece = new Piece(i, b);
			outPieces.add(piece);
		}
		
		return outPieces;
	}
}
