package com.github.rmheuer.azalea.serialization;

import com.github.rmheuer.azalea.serialization.graph.*;
import com.github.rmheuer.azalea.serialization.json.JsonDeserializer;
import com.github.rmheuer.azalea.serialization.json.JsonSerializer;
import com.github.rmheuer.azalea.serialization.object.AutoSerializable;
import com.github.rmheuer.azalea.serialization.object.ObjectSerializer;
import com.github.rmheuer.azalea.serialization.object.SerializationException;
import com.github.rmheuer.azalea.serialization.object.ValueSerializer;
import org.joml.Vector3f;

import java.util.Arrays;

public class Test {
    public static class Bar implements AutoSerializable {
        public int qux;

        @Override
        public String toString() {
            return "Bar{" +
                    "qux=" + qux +
                    '}';
        }
    }

    public static final class Garply extends Bar {
        public float i;

        public Garply() {}
        public Garply(float i) { this.i = i; }

        @Override
        public String toString() {
            return "Garply{" +
                    "qux=" + qux +
                    ", i=" + i +
                    '}';
        }
    }

    public static final class Foo implements AutoSerializable {
        public int bar;
        public Bar quux;
        public Bar qwert;
        public Bar nil;
        public String hoo;
        public int[] numpers;
        public int[][] numpers2;
        public Bar[][] bars;
        public Vector3f vec;

        @Override
        public String toString() {
            return "Foo{" +
                    "bar=" + bar +
                    ", quux=" + quux +
                    ", qwert=" + qwert +
                    ", nah=" + nil +
                    ", hoo='" + hoo + '\'' +
                    ", numpers=" + Arrays.toString(numpers) +
                    ", numpers2=" + Arrays.deepToString(numpers2) +
                    ", bars=" + Arrays.deepToString(bars) +
                    ", vec=" + vec +
                    '}';
        }
    }

    public static void main(String[] args) throws Exception {
        Foo foo = new Foo();
        foo.bar = 123;
        foo.quux = new Bar();
        foo.quux.qux = 1238947;
        foo.qwert = new Garply(57.8749754755f);
        foo.qwert.qux = 23;
        foo.nil = null;
        foo.hoo = "wooooo";
        foo.numpers = new int[] {9, 8, 7, 6};
        foo.numpers2 = new int[][] {
                {1, 2, 3, 4},
                {5, 6, 7, 8}
        };
        foo.bars = new Bar[][] {
                new Bar[] {new Bar()},
                new Garply[] {new Garply(3)}
        };
        foo.vec = new Vector3f(12.384719f, 3245.5f, 123948.55293f);

        ObjectSerializer serializer = new ObjectSerializer();
        serializer.registerSerializer(Vector3f.class, new ValueSerializer<Vector3f>() {
            @Override
            public Vector3f deserialize(ObjectSerializer serializer, DataNode node) throws SerializationException {
                ArrayNode arr = node.getAsArrayNode();
                return new Vector3f(
                        arr.get(0).getAsFloat(),
                        arr.get(1).getAsFloat(),
                        arr.get(2).getAsFloat()
                );
            }

            @Override
            public DataNode serialize(ObjectSerializer serializer, Vector3f value) throws SerializationException {
                ArrayNode arr = new ArrayNode();
                arr.add(new FloatNode(value.x));
                arr.add(new FloatNode(value.y));
                arr.add(new FloatNode(value.z));
                return arr;
            }
        });

        DataNode serialized = serializer.serialize(foo);
        System.out.println(serialized);

        String json = new JsonSerializer().write(serialized).replace("null", "narf");
        System.out.println(json);

        DataNode from = new JsonDeserializer(json).deserializeNode();
        Foo from2 = serializer.deserialize(from, Foo.class);

        System.out.println("Before: " + foo);
        System.out.println("After:  " + from2);
    }
}
