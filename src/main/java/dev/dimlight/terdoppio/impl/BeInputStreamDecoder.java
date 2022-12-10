package dev.dimlight.terdoppio.impl;

import dev.dimlight.terdoppio.api.BeDecoder;
import dev.dimlight.terdoppio.api.BeDecodingException;
import dev.dimlight.terdoppio.api.BeDictionary;
import dev.dimlight.terdoppio.api.BeInteger;
import dev.dimlight.terdoppio.api.BeList;
import dev.dimlight.terdoppio.api.BeString;
import dev.dimlight.terdoppio.api.BeValue;
import dev.dimlight.terdoppio.api.Bytes;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class BeInputStreamDecoder implements BeDecoder, AutoCloseable {

	public final static byte DELIM_INT_START = 'i';
	public final static byte DELIM_INT_END = 'e';
	public final static byte DELIM_DICT_START = 'd';
	public final static byte DELIM_DICT_END = 'e';
	public final static byte DELIM_LIST_START = 'l';
	public final static byte DELIM_LIST_END = 'e';
	public final static byte DELIM_STRING_LEN = ':';

	private final InputStream is;

	public BeInputStreamDecoder(InputStream is) {
		this.is = is;
	}

	@Override
	public void close() throws IOException {
		is.close();
	}

	@Override
	public BeDictionary decodeDictionary() throws BeDecodingException {
		consumeDelimiter(is, DELIM_DICT_START);
		final Map<BeString, BeValue> map = new TreeMap<>(BeString.COMPARATOR);

		while (peek(is) != DELIM_DICT_END) {
			BeString key = decodeString();
			BeValue value = consumeNextItem();
			map.put(key, value);
		}

		consumeDelimiter(is, DELIM_DICT_END);
		return new BeDictionaryImpl(map);
	}

	@Override
	public BeInteger decodeInteger() throws BeDecodingException {
		consumeDelimiter(is, DELIM_INT_START);
		String intAsString = consumeAsStringUntilDelimiter(is, DELIM_INT_END);
		try {
			final BigInteger val = new BigInteger(intAsString, 10);
			return new BeIntegerImpl(val);
		} catch (RuntimeException e) {
			throw new BeDecodingException(e);
		}
	}

	@Override
	public BeString decodeString() throws BeDecodingException {
		final String asciiNumbers = consumeAsStringUntilDelimiter(is, DELIM_STRING_LEN);
		int len = BeInputStreamDecoder.decodeASCIIInt(asciiNumbers);

		byte[] content = new byte[len];
		try {
			is.read(content, 0, len);
		} catch (IOException | ArrayIndexOutOfBoundsException e) {
			throw new BeDecodingException("Not enough data in buffer to read a string of len: [" + len + "] or IOException", e);
		}
		return new BeStringImpl(Bytes.of(content));
	}

	@Override
	public BeList decodeList() throws BeDecodingException {
		consumeDelimiter(is, DELIM_LIST_START);

		final List<BeValue> values = new ArrayList<>();

		while (peek(is) != DELIM_LIST_END) {
			final BeValue item = consumeNextItem();
			values.add(item);
		}

		consumeDelimiter(is, DELIM_LIST_END);
		return new BeListImpl(values);
	}

	//
	//
	//
	//
	//
	//
	//
	//

	/**
	 * Tries to consume the given byte from the buf.
	 * @throws dev.dimlight.terdoppio.api.BeDecodingException
	 */
	private static void consumeDelimiter(ByteBuffer buf, byte delim) throws BeDecodingException {
		byte foundDelim = buf.get();
		if (foundDelim != delim) {
			throw new BeDecodingException("Found unexpected delimiter: expected: [" + delim + "] found [" + foundDelim + "]");
		}
	}

	private static String consumeAsStringUntilDelimiter(ByteBuffer buf, byte delim) throws BeDecodingException {
		final StringBuilder out = new StringBuilder();

		byte b;
		do {
			try {
				b = buf.get();
			} catch (BufferUnderflowException e) {
				throw new BeDecodingException("Buffer underflow while consuming up to delimiter: [" + delim + "]");
			}

			if (b == delim) {
				break;
			}
			out.appendCodePoint(b & 0xFF);

		} while (true);

		return out.toString();
	}

	public static int decodeASCIIInt(String str) throws BeDecodingException {
		int value;
		try {
			value = Integer.parseInt(str);
		} catch (NumberFormatException e) {
			throw new BeDecodingException(e);
		}
		return value;
	}

	//
	// Input stream version of the decoding methods
	//

	/**
	 * Reads a byte from the stream without consuming it
	 * @param stream
	 * @return
	 * @throws dev.dimlight.terdoppio.api.BeDecodingException
	 */
	private static byte peek(InputStream stream) throws BeDecodingException {
		if (!stream.markSupported()) {
			throw new BeDecodingException("This parser need lookahead, hence a stream that supports mark() and reset()");
		}

		final byte b;
		try {
			stream.mark(1);
			b = (byte) stream.read(); // the read return an unsigned byte into a int, we can safely cast into a byte
			stream.reset();
		} catch (IOException e) {
			throw new BeDecodingException(e);
		}

		return b;
	}

	private static void consumeDelimiter(InputStream stream, byte delim) throws BeDecodingException {
		int foundDelim;
		try {
			foundDelim = stream.read();
		} catch (IOException e) {
			throw new BeDecodingException(e);
		}

		// end of the stream has been reached
		if (foundDelim == -1) {
			throw new BeDecodingException("End of the stream reached while expecting delim [" + delim + "]");
		}

		if (foundDelim != delim) {
			throw new BeDecodingException("Found unexpected delimiter: expected: [" + delim + "] found [" + foundDelim + "]");
		}
	}

	private static String consumeAsStringUntilDelimiter(InputStream stream, byte delim) throws BeDecodingException {
		final StringBuilder out = new StringBuilder();
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
			throw new BeDecodingException(e);
		}
		return out.toString();
	}

	public BeValue consumeNextItem() throws BeDecodingException {
		final byte nextByte = peek(is);
		return switch (nextByte) {
			case DELIM_DICT_START -> decodeDictionary();
			case DELIM_INT_START -> decodeInteger();
			case DELIM_LIST_START -> decodeList();
			case '1', '2', '3', '4', '5', '6', '7', '8', '9', '0' -> decodeString();
			default -> throw new BeDecodingException("Unexpected start of dictionary value: [" + nextByte + "]");
		};
	}
}
