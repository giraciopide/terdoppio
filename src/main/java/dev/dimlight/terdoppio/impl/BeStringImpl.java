package dev.dimlight.terdoppio.impl;

import dev.dimlight.terdoppio.api.BeString;
import dev.dimlight.terdoppio.api.BeValueType;
import dev.dimlight.terdoppio.api.Bytes;

import java.nio.charset.Charset;
import java.util.Objects;

public final class BeStringImpl implements BeString {

    private final Bytes content;

    public BeStringImpl(Bytes content) {
        this.content = content;
    }

    @Override
    public Bytes getBytes() {
        return content;
    }

    @Override
    public String asText(Charset charset) {
        return content.toString(charset);
    }

    @Override
    public BeValueType getValueType() {
        return BeValueType.STRING;
    }

    @Override
    public String toString() {
        return asText();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final BeStringImpl beString = (BeStringImpl) o;
        return Objects.equals(content, beString.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content);
    }
}
