package dev.dimlight.terdoppio.api;

import java.io.IOException;

public interface BeDecoder extends AutoCloseable {

    BeDictionary decodeDictionary() throws IOException;

    BeList decodeList() throws IOException;

    BeInteger decodeInteger() throws IOException;

    BeString decodeString() throws IOException;

    @Override
    void close() throws IOException;
}
