package com.github.rmheuer.azalea.serialization.object;

public interface SerializerProvider {
    // Return null if not supported
    <T> ValueSerializer<T> provide(Class<T> type);
}
