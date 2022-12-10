package dev.dimlight.terdoppio.api;

import java.util.Optional;
import java.util.SortedMap;

public interface BeDictionary extends BeValue {

    /**
     * @return an immutable map that contains the dictionary data.
     */
    SortedMap<BeString, BeValue> asMap();

    Optional<BeValue> get(BeString key);

    default Optional<BeValue> get(String textualKey) {
        return get(Be.string(textualKey));
    }

    default <V extends BeValue> Optional<BeValue> get(BeString key, Class<V> valueType) {
        return get(key)
            .map(valueType::cast);
    }

    default Optional<BeValueType> getValueType(BeString key) {
        return get(key)
            .map(BeValue::getValueType);
    }

    @Override
    default BeValueType getValueType() {
        return BeValueType.DICTIONARY;
    }
}
