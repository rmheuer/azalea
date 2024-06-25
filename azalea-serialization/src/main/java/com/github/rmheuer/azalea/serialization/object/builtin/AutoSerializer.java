package com.github.rmheuer.azalea.serialization.object.builtin;

import com.github.rmheuer.azalea.serialization.graph.DataNode;
import com.github.rmheuer.azalea.serialization.graph.ObjectNode;
import com.github.rmheuer.azalea.serialization.object.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public final class AutoSerializer<T extends AutoSerializable> implements ValueSerializer<T> {
    public static final SerializerProvider PROVIDER = new SerializerProvider() {
        @Override
        public <V> ValueSerializer<V> provide(Class<V> type) {
            if (!AutoSerializable.class.isAssignableFrom(type))
                return null;

            @SuppressWarnings({"unchecked", "rawtypes"})
            ValueSerializer<V> serializer = new AutoSerializer(type);
            return serializer;
        }
    };

    private static final class FieldInfo {
        final String name;
        final Field field;

        public FieldInfo(String name, Field field) {
            this.name = name;
            this.field = field;
        }
    }

    private final Constructor<T> constructor;
    private final List<FieldInfo> fields;

    private AutoSerializer(Class<T> type) {
        fields = new ArrayList<>();

        try {
            constructor = type.getDeclaredConstructor();
            if (!Modifier.isPublic(constructor.getModifiers()))
                throw new RuntimeException("No-args constructor is not public: " + constructor);

            for (Field field : type.getFields()) {
                int mod = field.getModifiers();
                if (Modifier.isPublic(mod) && !Modifier.isStatic(mod) && !Modifier.isFinal(mod) && !field.isSynthetic()) {
                    fields.add(new FieldInfo(field.getName(), field));
                }
            }
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to get serialization info for " + type, e);
        }
    }

    @Override
    public T deserialize(ObjectSerializer serializer, DataNode node) throws SerializationException {
        T value;
        try {
            value = constructor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new SerializationException("Failed to instantiate " + constructor.getDeclaringClass(), e);
        }

        ObjectNode obj = node.getAsObjectNode();
        for (FieldInfo field : fields) {
            if (!obj.has(field.name)) {
                System.err.println("Missing field: " + field.name);
                continue;
            }

            DataNode valueNode = obj.get(field.name);
            Object fieldValue = serializer.deserialize(valueNode, field.field.getType());

            try {
                field.field.set(value, fieldValue);
            } catch (ReflectiveOperationException e) {
                throw new SerializationException("Failed to set field " + field.field + " to " + fieldValue, e);
            }
        }

        return value;
    }

    @Override
    public DataNode serialize(ObjectSerializer serializer, T value) throws SerializationException {
        ObjectNode obj = new ObjectNode();
        for (FieldInfo field : fields) {
            Object fieldValue;
            try {
                fieldValue = field.field.get(value);
            } catch (ReflectiveOperationException e) {
                throw new SerializationException("Failed to get value of field " + field.field, e);
            }

            obj.put(field.name, serializer.serialize(fieldValue, field.field.getType()));
        }
        return obj;
    }
}
