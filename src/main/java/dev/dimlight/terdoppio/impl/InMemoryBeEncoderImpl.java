package dev.dimlight.terdoppio.impl;

import dev.dimlight.terdoppio.api.Be;
import dev.dimlight.terdoppio.api.BeDictionary;
import dev.dimlight.terdoppio.api.BeEncoder;
import dev.dimlight.terdoppio.api.BeInteger;
import dev.dimlight.terdoppio.api.BeList;
import dev.dimlight.terdoppio.api.BeString;
import dev.dimlight.terdoppio.api.BeValue;
import dev.dimlight.terdoppio.api.InMemoryBeEncoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class InMemoryBeEncoderImpl implements InMemoryBeEncoder {

    private final ByteArrayOutputStream baos;
    private final BeEncoder encoder;

    public InMemoryBeEncoderImpl() {
        baos = new ByteArrayOutputStream();
        encoder = Be.newEncoder(baos);
    }

    @Override
    public byte[] getBytes() {
        return baos.toByteArray();
    }

    //
    // delegate methods
    //

    @Override
    public void encode(BeDictionary dictionary) throws IOException {
        encoder.encode(dictionary);
    }

    @Override
    public void encode(BeList list) throws IOException {
        encoder.encode(list);
    }

    @Override
    public void encode(BeInteger integer) throws IOException {
        encoder.encode(integer);
    }

    @Override
    public void encode(BeString string) throws IOException {
        encoder.encode(string);
    }

    @Override
    public void encode(BeValue value) throws IOException {
        encoder.encode(value);
    }

    @Override
    public void close() throws IOException {
        encoder.close();
        baos.close();
    }
}
