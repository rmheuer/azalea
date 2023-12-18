package com.github.rmheuer.azalea.serialization.json;

public final class MinifiedTokenOutput extends JsonTokenOutput {
    private final StringBuilder builder;

    public MinifiedTokenOutput() {
        builder = new StringBuilder();
    }

    @Override
    public void beginObject() {
        builder.append('{');
    }

    @Override
    public void endObject() {
        builder.append('}');
    }

    @Override
    public void beginArray() {
        builder.append('[');
    }

    @Override
    public void endArray() {
        builder.append(']');
    }

    @Override
    public void objectKey(String key) {
        writeStringEscaped(builder, key);
        builder.append(":");
    }

    @Override
    public void comma() {
        builder.append(',');
    }

    @Override
    public void valNumberWhole(long number) {
        writeNumberWhole(builder, number);
    }

    @Override
    public void valNumberFloat(double number) {
        writeNumberFloat(builder, number);
    }

    @Override
    public void valString(String str) {
        writeStringEscaped(builder, str);
    }

    @Override
    public void valBool(boolean b) {
        builder.append(b);
    }

    @Override
    public void valNull() {
        builder.append("null");
    }

    @Override
    public String toString() {
        return builder.toString();
    }
}
