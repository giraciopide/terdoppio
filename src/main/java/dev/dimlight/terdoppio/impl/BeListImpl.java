package dev.dimlight.terdoppio.impl;

import dev.dimlight.terdoppio.api.BeList;
import dev.dimlight.terdoppio.api.BeListBuilder;
import dev.dimlight.terdoppio.api.BeValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BeListImpl implements BeList {

    private final List<BeValue> values;

    public BeListImpl(List<BeValue> values) {
        this.values = Collections.unmodifiableList(new ArrayList<>(values.size()));
    }

    @Override
    public List<BeValue> values() {
        return values;
    }

    @Override
    public Iterator<BeValue> iterator() {
        return values.iterator();
    }

    @Override
    public String toString() {
        return values.stream()
            .map(value -> "'" + value.toString() + "'")
            .collect(Collectors.joining(",", "[", "]"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final BeListImpl beValues = (BeListImpl) o;
        return Objects.equals(values, beValues.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values);
    }

    public static BeListBuilder builder() {
        return new Builder();
    }

    public static class Builder implements BeListBuilder {

        private final List<BeValue> values = new ArrayList<>();

        @Override
        public BeListBuilder add(BeValue value) {
            values.add(value);
            return this;
        }

        @Override
        public BeList build() {
            return new BeListImpl(values);
        }
    }
}
