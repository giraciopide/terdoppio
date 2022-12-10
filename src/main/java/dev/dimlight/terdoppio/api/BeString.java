package dev.dimlight.terdoppio.api;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;

public interface BeString extends BeValue {

    /**
     * A {@link dev.dimlight.terdoppio.api.BeString} is not comparable on its own (because I deem more important
     * to extend {@link dev.dimlight.terdoppio.api.BeValue} and not all values are comparable) but this comparator
     * can be used to compare strings as mandated by the bencode spec.
     */
    Comparator<BeString> COMPARATOR = Comparator.comparing(BeString::getBytes);

    Bytes getBytes();

    String asText(Charset charset);

    default String asTextExact() {
        return asTextExact(StandardCharsets.UTF_8);
    }

    default String asTextExact(Charset charset) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    default String asText() {
        return asText(StandardCharsets.UTF_8);
    }

    @Override
    default BeValueType getValueType() {
        return BeValueType.STRING;
    }
}
