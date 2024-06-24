package com.github.rmheuer.azalea.serialization.object.builtin;

import com.github.rmheuer.azalea.serialization.graph.ArrayNode;
import com.github.rmheuer.azalea.serialization.graph.DataNode;
import com.github.rmheuer.azalea.serialization.object.ObjectSerializer;
import com.github.rmheuer.azalea.serialization.object.SerializationException;
import com.github.rmheuer.azalea.serialization.object.SerializerProvider;
import com.github.rmheuer.azalea.serialization.object.ValueSerializer;

import java.lang.reflect.Array;

public final class ArraySerializer implements ValueSerializer<Object> {
    public static final SerializerProvider PROVIDER = new SerializerProvider() {
        @Override
        public <T> ValueSerializer<T> provide(Class<T> type) {
            if (!type.isArray())
                return null;

            @SuppressWarnings("unchecked")
            ValueSerializer<T> serializer = (ValueSerializer<T>) new ArraySerializer(type.getComponentType());
            return serializer;
        }
    };

    private final Class<?> valueType;

    private ArraySerializer(Class<?> valueType) {
        this.valueType = valueType;
    }

    @Override
    public Object deserialize(ObjectSerializer serializer, DataNode node) throws SerializationException {
        ArrayNode arr = node.getAsArrayNode();
        int size = arr.size();

        Object array = Array.newInstance(valueType, size);
        for (int i = 0; i < size; i++) {
            Array.set(array, i, serializer.deserialize(arr.get(i), valueType));
        }
        return array;
    }

    @Override
    public DataNode serialize(ObjectSerializer serializer, Object value) throws SerializationException {
        int size = Array.getLength(value);
        ArrayNode node = new ArrayNode();
        for (int i = 0; i < size; i++) {
            node.add(serializer.serialize(Array.get(value, i), valueType));
        }
        return node;
    }
}
