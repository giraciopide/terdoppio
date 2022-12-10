package dev.dimlight.terdoppio.api;

import java.math.BigInteger;

public interface BeInteger extends BeValue {

    long longValueExact();

    int intValueExact();

    BigInteger asBigInteger();

    @Override
    default BeValueType getValueType() {
        return BeValueType.INTEGER;
    }
}
