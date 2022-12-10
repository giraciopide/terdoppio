package dev.dimlight.terdoppio.api;

import java.io.IOException;

public interface BeEncoder extends AutoCloseable {

    void encode(BeDictionary dictionary) throws IOException;

    void encode(BeList list) throws IOException;

    void encode(BeInteger integer) throws IOException;

    void encode(BeString string) throws IOException;

    default void encode(BeValue value) throws IOException {
        switch (value.getValueType()) {
            case STRING -> encode(value.asString());
            case DICTIONARY -> encode(value.asDictionary());
            case INTEGER -> encode(value.asInteger());
            case LIST -> encode(value.asList());
            default -> throw new IOException("Cannot encode " + value.getClass().getName());
        }
    }

    @Override
    void close() throws IOException;
}
