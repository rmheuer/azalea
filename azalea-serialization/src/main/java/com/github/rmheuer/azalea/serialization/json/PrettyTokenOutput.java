package com.github.rmheuer.azalea.serialization.json;

// TODO: Compact objects and lists if they are short enough
// TODO: Lists should pack as many "simple" values into one line as possible (number, bool, short string, null)
public final class PrettyTokenOutput extends JsonTokenOutput {
    private static final String INDENT = "  ";

    private final StringBuilder builder;
    private String currentIndent;

    public PrettyTokenOutput() {
        builder = new StringBuilder();
        currentIndent = "";
    }
    
    private void indent() {
        currentIndent += INDENT;
    }

    private void unindent() {
        currentIndent = currentIndent.substring(INDENT.length());
    }

    private void newLine() {
        builder.append("\n");
        builder.append(currentIndent);
    }
    
    @Override
    public void beginObject() {
        builder.append("{");
        indent();
        newLine();
    }

    @Override
    public void endObject() {
        unindent();
        newLine();
        builder.append("}");
    }

    @Override
    public void beginArray() {
        builder.append("[");
        indent();
        newLine();
    }

    @Override
    public void endArray() {
        unindent();
        newLine();
        builder.append("]");
    }

    @Override
    public void objectKey(String key) {
        writeStringEscaped(builder, key);
        builder.append(": ");
    }

    @Override
    public void comma() {
        builder.append(",");
        newLine();
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
