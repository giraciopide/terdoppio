package com.wazzanau.bencoding;

import dev.dimlight.terdoppio.api.Be;
import dev.dimlight.terdoppio.api.BeDecoder;
import dev.dimlight.terdoppio.api.BeDictionary;
import dev.dimlight.terdoppio.api.BeInteger;
import dev.dimlight.terdoppio.api.BeList;
import dev.dimlight.terdoppio.api.BeString;
import dev.dimlight.terdoppio.api.InMemoryBeEncoder;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author Marco Nicolini
 */
public class RoundTripTests {

    private static final Logger log = LoggerFactory.getLogger(RoundTripTests.class);

    @Test
    public void testIntegerRoundtrips() throws Exception {
        testRoundtrip(0);
        testRoundtrip(1);
        testRoundtrip(-1);

        for (int i = -100000; i < 100000; i++) {
            testRoundtrip(i);
        }
    }

    private static void testRoundtrip(long value) throws IOException {
        final BeInteger i = Be.integer(value);
        try (final InMemoryBeEncoder encoder = Be.newInMemoryEncoder()) {

            encoder.encode(i);

            final byte[] bytes = encoder.getBytes();
            // log.info("encoded form [{}]", HexFormat.ofDelimiter(" ").formatHex(bytes));

            try (final BeDecoder decoder = Be.fromMemory(bytes)) {
                final BeInteger j = decoder.decodeInteger();

                Assert.assertEquals(i, j);
            }
        }
    }

    @Test
    public void testStringRoundtrip() throws IOException {
        testStringRoundTrip("Hello there how are you?!?".getBytes(StandardCharsets.UTF_8));

        testStringRoundTrip(new byte[] {-1, 0, 1, Byte.MIN_VALUE, Byte.MAX_VALUE});
    }

    private static void testStringRoundTrip(byte[] content) throws IOException {
        final BeString value = Be.string(content);
        try (final InMemoryBeEncoder encoder = Be.newInMemoryEncoder()) {

            encoder.encode(value);

            final byte[] bytes = encoder.getBytes();
            // log.info("encoded form [{}]", HexFormat.ofDelimiter(" ").formatHex(bytes));

            try (final BeDecoder decoder = Be.fromMemory(bytes)) {
                final BeString j = decoder.decodeString();

                Assert.assertEquals(value, j);
            }
        }
    }

    @Test
    public void testListRoundTrip() throws IOException {
        testListRoundtrip(Be.list().build());

        testListRoundtrip(Be.list()
                              .add(Be.integer(340))
                              .add(Be.string("a string"))
                              .add(Be.list().build())
                              .add(Be.list()
                                       .add(Be.integer(43))
                                       .build())
                              .add(Be.dictionary()
                                       .put(Be.string("key1"), Be.integer(123))
                                       .put(Be.string("key2"), Be.string("value!"))
                                       .build())
                              .build());
    }

    private static void testListRoundtrip(BeList list) throws IOException {
        try (final InMemoryBeEncoder encoder = Be.newInMemoryEncoder()) {

            encoder.encode(list);

            final byte[] bytes = encoder.getBytes();
            // log.info("encoded form [{}]", HexFormat.ofDelimiter(" ").formatHex(bytes));

            try (final BeDecoder decoder = Be.fromMemory(bytes)) {
                final BeList j = decoder.decodeList();

                Assert.assertEquals(list, j);
            }
        }
    }

    @Test
    public void testDictRoundtrips() throws IOException {
        testDictRoundtrip(Be.dictionary()
                              .put(Be.string("x1"), Be.string("value"))
                              .put(Be.string("x2"), Be.string("value"))
                              .put(Be.string("x3"), Be.string("value"))
                              .put(Be.string("x4"), Be.string("value"))
                              .put(Be.string("x5"), Be.string("value"))
                              .build());
    }

    private static void testDictRoundtrip(BeDictionary dict) throws IOException {
        try (final InMemoryBeEncoder encoder = Be.newInMemoryEncoder()) {

            encoder.encode(dict);

            final byte[] bytes = encoder.getBytes();
            // log.info("encoded form [{}]", HexFormat.ofDelimiter(" ").formatHex(bytes));

            try (final BeDecoder decoder = Be.fromMemory(bytes)) {
                final BeDictionary j = decoder.decodeDictionary();

                Assert.assertEquals(dict, j);
            }
        }
    }
}
