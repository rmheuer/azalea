package com.github.rmheuer.azalea.serialization.object;

import com.github.rmheuer.azalea.serialization.graph.DataNode;

public interface ValueSerializer<T> {
    T deserialize(ObjectSerializer serializer, DataNode node) throws SerializationException;
    DataNode serialize(ObjectSerializer serializer, T value) throws SerializationException;
}
