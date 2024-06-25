package com.github.rmheuer.azalea.serialization.object;

import com.github.rmheuer.azalea.serialization.graph.DataNode;
import com.github.rmheuer.azalea.serialization.graph.NullNode;
import com.github.rmheuer.azalea.serialization.graph.ObjectNode;
import com.github.rmheuer.azalea.serialization.graph.StringNode;
import com.github.rmheuer.azalea.serialization.object.builtin.ArraySerializer;
import com.github.rmheuer.azalea.serialization.object.builtin.AutoSerializer;
import com.github.rmheuer.azalea.serialization.object.builtin.PrimitiveSerializers;
import com.github.rmheuer.azalea.utils.ReflectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ObjectSerializer {
    private static final String CLASS_SPECIFIER_KEY = "class";
    private static final String VALUE_KEY = "=";

    private final Map<Class<?>, ValueSerializer<?>> serializers;
    private final List<SerializerProvider> providers;

    public ObjectSerializer() {
        serializers = new HashMap<>();
        providers = new ArrayList<>();

        PrimitiveSerializers.register(this);
        registerProvider(AutoSerializer.PROVIDER);
        registerProvider(ArraySerializer.PROVIDER);
    }

    public void registerProvider(SerializerProvider provider) {
        // Providers added later take priority
        providers.add(0, provider);
    }

    public <T> void registerSerializer(Class<T> type, ValueSerializer<T> serializer) {
        serializers.put(type, serializer);
    }

    private <T> ValueSerializer<T> getSerializer(Class<T> type) {
        // Guaranteed to be correct, serializers are always mapped from a class of their type
        @SuppressWarnings("unchecked")
        ValueSerializer<T> serializer = (ValueSerializer<T>) serializers.get(type);
        if (serializer != null)
            return serializer;

        for (SerializerProvider provider : providers) {
            ValueSerializer<T> s = provider.provide(type);
            if (s == null)
                continue;

            serializers.put(type, s);
            return s;
        }

        return null;
    }

    private <T> DataNode serializeRaw(Class<T> type, Object value) throws SerializationException {
        @SuppressWarnings("unchecked")
        T t = (T) value;
        ValueSerializer<T> serializer = getSerializer(type);
        if (serializer == null)
            throw new SerializationException("No serializer defined for " + type);
        return serializer.serialize(this, t);
    }

    public <T> DataNode serialize(T value, Class<?> inferredType) throws SerializationException {
        if (value == null)
            return NullNode.INSTANCE;

        Class<?> actualType = value.getClass();
        DataNode raw = serializeRaw(actualType, value);

        // Don't need to annotate it, type can be inferred when deserializing
        if (ReflectUtils.sameBoxed(actualType, inferredType))
            return raw;

        ObjectNode obj = new ObjectNode();
        obj.put(CLASS_SPECIFIER_KEY, new StringNode(actualType.getName()));
        if (raw.isObjectNode()) {
            obj.putAll(raw.getAsObjectNode());
        } else {
            obj.put(VALUE_KEY, raw);
        }
        return obj;
    }

    public boolean isSerializable(Class<?> type) {
        return getSerializer(type) != null;
    }

    public <T> T deserialize(DataNode data, Class<T> type) throws SerializationException {
        if (data.isNullNode())
            return null;

        Class<?> serializedType = type;

        // Handle potential polymorphism
        if (data.isObjectNode()) {
            ObjectNode obj = data.getAsObjectNode();
            if (obj.has(CLASS_SPECIFIER_KEY)) {
                String className = obj.get(CLASS_SPECIFIER_KEY).getAsString();
                try {
                    serializedType = Class.forName(className);
                } catch (ClassNotFoundException e) {
                    throw new SerializationException("Class not found: " + className, e);
                }

                if (obj.has(VALUE_KEY))
                    data = obj.get(VALUE_KEY);
            }
        }

        ValueSerializer<?> serializer = getSerializer(serializedType);
        if (serializer == null)
            throw new SerializationException("No serializer defined for " + type);
        Object value = serializer.deserialize(this, data);

        @SuppressWarnings("unchecked")
        T t = (T) value;

        return t;
    }

    /**
     * Serializes a value, assuming its type will always be known when
     * deserializing. For example, a final class would be guaranteed to have a
     * known type because it cannot be extended.
     *
     * @param value value to serialize
     * @return serialized data
     */
    public DataNode serialize(Object value) throws SerializationException {
        return serialize(value, value.getClass());
    }
}
