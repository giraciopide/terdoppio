package dev.dimlight.terdoppio.api;

public interface BeDictionaryBuilder {

    BeDictionaryBuilder put(BeString key, BeValue value);

    BeDictionary build();
}
