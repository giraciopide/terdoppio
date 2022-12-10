package dev.dimlight.terdoppio.impl;

import dev.dimlight.terdoppio.api.BeDictionary;
import dev.dimlight.terdoppio.api.BeEncoder;
import dev.dimlight.terdoppio.api.BeInteger;
import dev.dimlight.terdoppio.api.BeList;
import dev.dimlight.terdoppio.api.BeString;
import dev.dimlight.terdoppio.api.BeValue;
import dev.dimlight.terdoppio.api.Bytes;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class BeOutputStreamEncoder implements BeEncoder, AutoCloseable {

    private final OutputStream os;

    private static final int DELIM_DICT_START = toUint8((byte) 'd');
    private static final int DELIM_DICT_END = toUint8((byte) 'e');
    private static final int DELIM_LIST_START = toUint8((byte) 'l');
    private static final int DELIM_LIST_END = toUint8((byte) 'e');
    private static final int DELIM_INT_START = toUint8((byte) 'i');
    private static final int DELIM_INT_END = toUint8((byte) 'e');
    private static final int DELIM_STRING_LEN = toUint8((byte) ':');

    public BeOutputStreamEncoder(OutputStream os) {
        this.os = os;
    }

    /**
     * Converts a java byte (signed 8 bit) to an int containing the unsigned byte representation
     * @param b
     * @return an int where the value of the given signed byte is interpreted as unsigned byte
     */
    private static int toUint8(byte b) {
        return (int)b & 0xFF;
    }

    @Override
    public void encode(BeDictionary dictionary) throws IOException {
        os.write(DELIM_DICT_START);
        for (Map.Entry<BeString, BeValue> entry: dictionary.asMap().entrySet()) {
            encode(entry.getKey());
            encode(entry.getValue());
        }
        os.write(DELIM_DICT_END);
    }

    @Override
    public void encode(BeList list) throws IOException {
        os.write(DELIM_LIST_START);
        for (BeValue item: list) {
            encode(item);
        }
        os.write(DELIM_LIST_END);
    }

    @Override
    public void encode(BeInteger integer) throws IOException {
        os.write(DELIM_INT_START);
        os.write(integer.asBigInteger().toString().getBytes(StandardCharsets.UTF_8));
        os.write(DELIM_INT_END);
    }

    @Override
    public void encode(BeString string) throws IOException {
        final Bytes bytes = string.getBytes();

        os.write(Integer.toString(bytes.length()).getBytes(StandardCharsets.UTF_8));
        os.write(DELIM_STRING_LEN);
        bytes.writeTo(os);
    }

    @Override
    public void close() throws IOException {
        this.os.close();
    }
}
