package dev.dimlight.terdoppio.impl;

import dev.dimlight.terdoppio.api.BeInteger;
import dev.dimlight.terdoppio.api.BeValueType;

import java.math.BigInteger;
import java.util.Objects;

public class BeIntegerImpl implements BeInteger {

    private final BigInteger value;

    public BeIntegerImpl(BigInteger value) {
        this.value = value;
    }

    @Override
    public long longValueExact() {
        return value.longValueExact();
    }

    @Override
    public int intValueExact() {
        return value.intValueExact();
    }

    @Override
    public BigInteger asBigInteger() {
        return value;
    }

    @Override
    public BeValueType getValueType() {
        return BeValueType.INTEGER;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final BeIntegerImpl beInteger = (BeIntegerImpl) o;
        return Objects.equals(value, beInteger.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
