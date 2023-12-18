package com.github.rmheuer.azalea.serialization.json;

public abstract class JsonTokenOutput {
    public abstract void beginObject();
    public abstract void endObject();
    public abstract void beginArray();
    public abstract void endArray();

    public abstract void objectKey(String key);
    public abstract void comma();

    public abstract void valNumberWhole(long number);
    public abstract void valNumberFloat(double number);
    public abstract void valString(String str);
    public abstract void valBool(boolean b);
    public abstract void valNull();

    protected void writeNumberWhole(StringBuilder builder, long number) {
        builder.append(number);
    }

    protected void writeNumberFloat(StringBuilder builder, double number) {
        // Technically not JSON spec compliant because of NaN, -0, and +-Infinity
        builder.append(number);
    }

    protected void writeStringEscaped(StringBuilder builder, String str) {
        builder.append('"');
        for (char c : str.toCharArray()) {
            switch (c) {
                case '"': builder.append("\\\""); break;
                case '\\': builder.append("\\\\"); break;
                case '\b': builder.append("\\b"); break;
                case '\f': builder.append("\\f"); break;
                case '\n': builder.append("\\n"); break;
                case '\r': builder.append("\\r"); break;
                case '\t': builder.append("\\t"); break;
                default:
                    if (c < 0x0020) {
                        builder.append(String.format("\\u%04x", (int) c));
                    } else {
                        builder.append(c);
                    }
                    break;
            }
        }
        builder.append('"');
    }
}
