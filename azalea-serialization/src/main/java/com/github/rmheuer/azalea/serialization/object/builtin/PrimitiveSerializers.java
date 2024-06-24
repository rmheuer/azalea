package com.github.rmheuer.azalea.serialization.object.builtin;

import com.github.rmheuer.azalea.serialization.graph.*;
import com.github.rmheuer.azalea.serialization.object.ObjectSerializer;
import com.github.rmheuer.azalea.serialization.object.ValueSerializer;

public final class PrimitiveSerializers {
    public static void register(ObjectSerializer serializer) {
        serializer.registerSerializer(boolean.class, BOOLEAN);
        serializer.registerSerializer(byte.class, BYTE);
        serializer.registerSerializer(short.class, SHORT);
        serializer.registerSerializer(int.class, INT);
        serializer.registerSerializer(long.class, LONG);
        serializer.registerSerializer(float.class, FLOAT);
        serializer.registerSerializer(double.class, DOUBLE);
        serializer.registerSerializer(char.class, CHAR);

        serializer.registerSerializer(Boolean.class, BOOLEAN);
        serializer.registerSerializer(Byte.class, BYTE);
        serializer.registerSerializer(Short.class, SHORT);
        serializer.registerSerializer(Integer.class, INT);
        serializer.registerSerializer(Long.class, LONG);
        serializer.registerSerializer(Float.class, FLOAT);
        serializer.registerSerializer(Double.class, DOUBLE);
        serializer.registerSerializer(Character.class, CHAR);

        serializer.registerSerializer(String.class, STRING);
    }

    private static final ValueSerializer<Boolean> BOOLEAN = new ValueSerializer<Boolean>() {
        @Override
        public Boolean deserialize(ObjectSerializer serializer, DataNode node) {
            return node.getAsBoolean();
        }

        @Override
        public DataNode serialize(ObjectSerializer serializer, Boolean value) {
            return new BooleanNode(value);
        }
    };

    private static final ValueSerializer<Byte> BYTE = new ValueSerializer<Byte>() {
        @Override
        public Byte deserialize(ObjectSerializer serializer, DataNode node) {
            return node.getAsNumber().byteValue();
        }

        @Override
        public DataNode serialize(ObjectSerializer serializer, Byte value) {
            return new ByteNode(value);
        }
    };

    private static final ValueSerializer<Short> SHORT = new ValueSerializer<Short>() {
        @Override
        public Short deserialize(ObjectSerializer serializer, DataNode node) {
            return node.getAsNumber().shortValue();
        }

        @Override
        public DataNode serialize(ObjectSerializer serializer, Short value) {
            return new ShortNode(value);
        }
    };

    private static final ValueSerializer<Integer> INT = new ValueSerializer<Integer>() {
        @Override
        public Integer deserialize(ObjectSerializer serializer, DataNode node) {
            return node.getAsNumber().intValue();
        }

        @Override
        public DataNode serialize(ObjectSerializer serializer, Integer value) {
            return new IntNode(value);
        }
    };

    private static final ValueSerializer<Long> LONG = new ValueSerializer<Long>() {
        @Override
        public Long deserialize(ObjectSerializer serializer, DataNode node) {
            return node.getAsNumber().longValue();
        }

        @Override
        public DataNode serialize(ObjectSerializer serializer, Long value) {
            return new LongNode(value);
        }
    };

    private static final ValueSerializer<Float> FLOAT = new ValueSerializer<Float>() {
        @Override
        public Float deserialize(ObjectSerializer serializer, DataNode node) {
            return node.getAsNumber().floatValue();
        }

        @Override
        public DataNode serialize(ObjectSerializer serializer, Float value) {
            return new FloatNode(value);
        }
    };

    private static final ValueSerializer<Double> DOUBLE = new ValueSerializer<Double>() {
        @Override
        public Double deserialize(ObjectSerializer serializer, DataNode node) {
            return node.getAsNumber().doubleValue();
        }

        @Override
        public DataNode serialize(ObjectSerializer serializer, Double value) {
            return new DoubleNode(value);
        }
    };

    private static final ValueSerializer<Character> CHAR = new ValueSerializer<Character>() {
        @Override
        public Character deserialize(ObjectSerializer serializer, DataNode node) {
            return node.getAsChar();
        }

        @Override
        public DataNode serialize(ObjectSerializer serializer, Character value) {
            return new CharNode(value);
        }
    };

    private static final ValueSerializer<String> STRING = new ValueSerializer<String>() {
        @Override
        public String deserialize(ObjectSerializer serializer, DataNode node) {
            return node.getAsString();
        }

        @Override
        public DataNode serialize(ObjectSerializer serializer, String value) {
            return new StringNode(value);
        }
    };
}
