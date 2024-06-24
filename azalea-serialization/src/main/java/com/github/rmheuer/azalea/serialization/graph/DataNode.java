package com.github.rmheuer.azalea.serialization.graph;

public interface DataNode {
    default boolean isObjectNode() {
        return this instanceof ObjectNode;
    }

    default boolean isNullNode() { return this instanceof NullNode; }

    default NumberNode getAsNumberNode() {
        return (NumberNode) this;
    }

    default TextNode getAsTextNode() {
        return (TextNode) this;
    }

    default BooleanNode getAsBooleanNode() {
        return (BooleanNode) this;
    }

    default ObjectNode getAsObjectNode() {
        return (ObjectNode) this;
    }

    default ArrayNode getAsArrayNode() { return (ArrayNode) this; }

    default Number getAsNumber() {
        return getAsNumberNode().getAsNumber();
    }

    default float getAsFloat() { return getAsNumberNode().getAsFloat(); }

    default char getAsChar() {
        return getAsTextNode().getAsChar();
    }

    default String getAsString() {
        return getAsTextNode().getAsString();
    }

    default boolean getAsBoolean() {
        return getAsBooleanNode().getValue();
    }
}
