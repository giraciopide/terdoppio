package dev.dimlight.terdoppio.api;

import dev.dimlight.terdoppio.impl.BeDictionaryImpl;
import dev.dimlight.terdoppio.impl.BeInputStreamDecoder;
import dev.dimlight.terdoppio.impl.BeIntegerImpl;
import dev.dimlight.terdoppio.impl.BeListImpl;
import dev.dimlight.terdoppio.impl.BeOutputStreamEncoder;
import dev.dimlight.terdoppio.impl.BeStringImpl;
import dev.dimlight.terdoppio.impl.InMemoryBeEncoderImpl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

/**
 * Entry point to the bencoding API.
 */
public final class Be {

    private Be() {}

    public static BeDecoder newDecoder(InputStream inputStream) {
        return new BeInputStreamDecoder(inputStream);
    }

    public static BeEncoder newEncoder(OutputStream outputStream) {
        return new BeOutputStreamEncoder(outputStream);
    }

    public static InMemoryBeEncoder newInMemoryEncoder() {
        return new InMemoryBeEncoderImpl();
    }

    public static BeDecoder fromMemory(byte[] bytes) {
        return Be.newDecoder(new ByteArrayInputStream(bytes));
    }

    public static BeDictionaryBuilder dictionary() {
        return new BeDictionaryImpl.Builder();
    }

    public static BeListBuilder list() {
        return new BeListImpl.Builder();
    }

    public static BeString string(Bytes content) {
        return new BeStringImpl(content);
    }

    public static BeString string(byte[] content) {
        return new BeStringImpl(Bytes.of(content));
    }

    public static BeString string(String content) {
        return new BeStringImpl(Bytes.of(content));
    }

    public static BeInteger integer(long value) {
        return new BeIntegerImpl(BigInteger.valueOf(value));
    }

    public static BeInteger integer(int value) {
        return new BeIntegerImpl(BigInteger.valueOf(value));
    }

    public static BeInteger integer(BigInteger value) {
        return new BeIntegerImpl(value);
    }
}
