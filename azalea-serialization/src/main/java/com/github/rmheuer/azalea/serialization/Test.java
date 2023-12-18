package com.github.rmheuer.azalea.serialization;

import com.github.rmheuer.azalea.serialization.graph.*;
import com.github.rmheuer.azalea.serialization.json.JsonSerializer;
import com.github.rmheuer.azalea.serialization.json.PrettyTokenOutput;

public class Test {
    public static void main(String[] args) {
        ArrayNode arr = new ArrayNode();
        arr.add(new ByteNode((byte) 13));
        arr.add(new ShortNode((short) 32767));
        arr.add(new IntNode(83730910));
        arr.add(new LongNode(1234567890));
        arr.add(new FloatNode(123.45f));
        arr.add(new DoubleNode(67.89));

        ObjectNode obj = new ObjectNode();
        obj.put("array", arr);
        obj.put("is cool", new BooleanNode(true));
        obj.put("boring", NullNode.INSTANCE);
        obj.put("basic string", new StringNode("hi im a basic string"));
        obj.put("wacky string", new StringNode("woow \\ \b \u0003 \r\n\f\t\" special chars!"));
        obj.put("one char", new CharNode('1'));

        PrettyTokenOutput out = new PrettyTokenOutput();
        JsonSerializer serializer = new JsonSerializer(out);
        serializer.serialize(obj);

        System.out.println(out);
    }
}
