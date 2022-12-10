package dev.dimlight.terdoppio.api;

public interface BeValue {

    BeValueType getValueType();

    default BeString asString() {
        return tryCast(this, BeString.class);
    }

    default BeList asList() {
        return tryCast(this, BeList.class);
    }

    default BeDictionary asDictionary() {
        return tryCast(this, BeDictionary.class);
    }

    default BeInteger asInteger() {
        return tryCast(this, BeInteger.class);
    }

    /**
     * @param <F> from
     * @param <T> to
     */
    private static <F, T> T tryCast(F from, Class<T> toClass) {
        if (toClass.isAssignableFrom(from.getClass())) {
            return toClass.cast(from);
        }
        throw new ClassCastException(from.getClass().getName() + " cannot be cast to " + toClass.getName());
    }
}
