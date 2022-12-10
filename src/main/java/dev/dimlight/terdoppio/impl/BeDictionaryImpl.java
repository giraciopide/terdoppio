package dev.dimlight.terdoppio.impl;

import dev.dimlight.terdoppio.api.BeDictionary;
import dev.dimlight.terdoppio.api.BeDictionaryBuilder;
import dev.dimlight.terdoppio.api.BeString;
import dev.dimlight.terdoppio.api.BeValue;
import dev.dimlight.terdoppio.api.BeValueType;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class BeDictionaryImpl implements BeDictionary {

    private final SortedMap<BeString, BeValue> dict;

    private final SortedMap<BeString, BeValue> dictView;

    public BeDictionaryImpl(Map<BeString, BeValue> dict) {
        this.dict = new TreeMap<>(BeStringImpl.COMPARATOR);
        this.dict.putAll(dict);
        this.dictView = Collections.unmodifiableSortedMap(this.dict);
    }

    @Override
    public SortedMap<BeString, BeValue> asMap() {
        return dictView;
    }

    @Override
    public Optional<BeValue> get(BeString key) {
        return Optional.ofNullable(dict.get(key));
    }

    @Override
    public BeValueType getValueType() {
        return BeValueType.DICTIONARY;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final BeDictionaryImpl that = (BeDictionaryImpl) o;
        return dict.equals(that.dict) && Objects.equals(dictView, that.dictView);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dict);
    }

    @Override
    public String toString() {
        return dict.entrySet().stream()
            .map(kv -> "'" + kv.getKey().asText() + "': '" + kv.getValue().toString() + "'")
            .collect(Collectors.joining(",", "{", "}"));
    }

    public static BeDictionaryBuilder builder() {
        return new Builder();
    }

    public static class Builder implements BeDictionaryBuilder {

        private final Map<BeString, BeValue> entries = new TreeMap<>(BeString.COMPARATOR);

        @Override
        public BeDictionaryBuilder put(BeString key, BeValue value) {
            entries.put(key, value);
            return this;
        }

        @Override
        public BeDictionary build() {
            return new BeDictionaryImpl(entries);
        }
    }
}
