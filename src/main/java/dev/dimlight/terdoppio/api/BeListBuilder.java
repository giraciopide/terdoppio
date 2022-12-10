package dev.dimlight.terdoppio.api;

public interface BeListBuilder {

    BeListBuilder add(BeValue value);

    BeList build();
}
