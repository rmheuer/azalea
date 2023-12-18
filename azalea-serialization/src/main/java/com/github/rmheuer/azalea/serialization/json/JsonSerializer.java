package com.github.rmheuer.azalea.serialization.json;

import com.github.rmheuer.azalea.serialization.graph.*;

public final class JsonSerializer {
    private final JsonTokenOutput output;

    public JsonSerializer(JsonTokenOutput output) {
        this.output = output;
    }

    private void serializeArray(ArrayNode arrayNode) {
        output.beginArray();

        boolean comma = false;
        for (DataNode node : arrayNode) {
            if (comma) output.comma();
            else comma = true;

            serialize(node);
        }

        output.endArray();
    }

    private void serializeObject(ObjectNode objectNode) {
        output.beginObject();

        boolean comma = false;
        for (String key : objectNode.keySet()) {
            if (comma) output.comma();
            else comma = true;

            output.objectKey(key);
            serialize(objectNode.get(key));
        }

        output.endObject();
    }

    private void serializeNumber(NumberNode numberNode) {
        if (numberNode.isWholeNumber())
            output.valNumberWhole(numberNode.getAsNumber().longValue());
        else
            output.valNumberFloat(numberNode.getAsNumber().doubleValue());
    }

    private void serializeBoolean(BooleanNode booleanNode) {
        output.valBool(booleanNode.getValue());
    }

    private void serializeText(TextNode charNode) {
        output.valString(charNode.getAsString());
    }

    public void serialize(DataNode node) {
        if (node instanceof ArrayNode)
            serializeArray((ArrayNode) node);
        else if (node instanceof ObjectNode)
            serializeObject((ObjectNode) node);
        else if (node instanceof NumberNode)
            serializeNumber((NumberNode) node);
        else if (node instanceof BooleanNode)
            serializeBoolean((BooleanNode) node);
        else if (node instanceof TextNode)
            serializeText((TextNode) node);
        else if (node == NullNode.INSTANCE)
            output.valNull();
        else
            throw new IllegalArgumentException("Unsupported node type: " + node.getClass().getSimpleName());
    }
}
