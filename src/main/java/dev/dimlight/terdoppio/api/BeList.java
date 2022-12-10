package dev.dimlight.terdoppio.api;

import java.util.List;

public interface BeList extends BeValue, Iterable<BeValue> {

    List<BeValue> values();

    @Override
    default BeValueType getValueType() {
        return BeValueType.LIST;
    }
}
