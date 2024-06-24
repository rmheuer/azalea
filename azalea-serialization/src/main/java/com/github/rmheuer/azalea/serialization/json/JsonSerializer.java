package com.github.rmheuer.azalea.serialization.json;

import com.github.rmheuer.azalea.serialization.graph.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class JsonSerializer {
    private static final int IDEAL_MAX_LINE_LEN = 80;
    private static final String INDENT = "  ";
    
    private static abstract class Writer {
        protected abstract void serializeArray(StringBuilder builder, ArrayNode arrayNode, String indent);
        protected abstract void serializeObject(StringBuilder builder, ObjectNode objectNode, String indent);

        private void serializeNumber(StringBuilder builder, NumberNode numberNode) {
            builder.append(numberNode.getAsNumber());
        }

        private void serializeBoolean(StringBuilder builder, BooleanNode booleanNode) {
            builder.append(booleanNode.getValue() ? "true" : "false");
        }

        private void serializeText(StringBuilder builder, TextNode textNode) {
            writeStringEscaped(builder, textNode.getAsString());
        }

        protected void serializeNode(StringBuilder builder, DataNode node, String indent) {
            if (node instanceof ArrayNode)
                serializeArray(builder, (ArrayNode) node, indent);
            else if (node instanceof ObjectNode)
                serializeObject(builder, (ObjectNode) node, indent);
            else if (node instanceof NumberNode)
                serializeNumber(builder, (NumberNode) node);
            else if (node instanceof BooleanNode)
                serializeBoolean(builder, (BooleanNode) node);
            else if (node instanceof TextNode)
                serializeText(builder, (TextNode) node);
            else if (node == NullNode.INSTANCE)
                builder.append("null");
            else
                throw new IllegalArgumentException("Unsupported node type: " + node.getClass().getSimpleName());
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

        public String write(DataNode node, String indent) {
            StringBuilder builder = new StringBuilder();
            serializeNode(builder, node, indent);
            return builder.toString();
        }
    }

    private static final class WriterDense extends Writer {
        @Override
        protected void serializeArray(StringBuilder builder, ArrayNode arrayNode, String indent) {
            builder.append("[ ");

            boolean comma = false;
            for (DataNode node : arrayNode) {
                if (comma) builder.append(", ");
                else comma = true;

                serializeNode(builder, node, indent);
            }

            builder.append(" ]");
        }

        @Override
        protected void serializeObject(StringBuilder builder, ObjectNode objectNode, String indent) {
            builder.append("{ ");

            boolean comma = false;
            for (String key : objectNode.keySet()) {
                if (comma) builder.append(", ");
                else comma = true;

                writeStringEscaped(builder, key);
                builder.append(": ");
                serializeNode(builder, objectNode.get(key), indent);
            }

            builder.append(" }");
        }
    }

    private final WriterDense dense = new WriterDense();
    private final WriterPretty pretty = new WriterPretty();

    private final class WriterPretty extends Writer {
        @Override
        protected void serializeArray(StringBuilder builder, ArrayNode arrayNode, String indent) {
            List<String> items = new ArrayList<>();
            int size = arrayNode.size();
            String nextIndent = indent + INDENT;
            for (int i = 0; i < size; i++) {
                items.add(JsonSerializer.this.write(arrayNode.get(i), nextIndent, i == size - 1 ? 0 : 1));
            }
            writePretty(builder, '[', ']', items, indent);
        }

        @Override
        protected void serializeObject(StringBuilder builder, ObjectNode objectNode, String indent) {
            List<String> items = new ArrayList<>();

            Set<String> keys = objectNode.keySet();
            int length = keys.size();
            String nextIndent = indent + INDENT;
            int i = 0;
            for (String key : keys) {
                StringBuilder itemBuilder = new StringBuilder();
                writeStringEscaped(itemBuilder, key);
                itemBuilder.append(": ");
                itemBuilder.append(JsonSerializer.this.write(objectNode.get(key), nextIndent, itemBuilder.length() + (i == length - 1 ? 0 : 1)));
                items.add(itemBuilder.toString());

                i++;
            }

            writePretty(builder, '{', '}', items, indent);
        }

        private void writePretty(StringBuilder builder, char start, char end, List<String> items, String indent) {
            String nextIndent = indent + INDENT;

            builder.append(start);
            if (items.size() > 0) {
                builder.append('\n');
                boolean comma = false;
                for (String item : items) {
                    if (comma) builder.append(",\n");
                    else comma = true;
                    builder.append(nextIndent);
                    builder.append(item);
                }
                builder.append('\n');
                builder.append(indent);
            }
            builder.append(end);
        }
    }
    
    private String write(DataNode node, String indent, int reserved) {
        int availLength = IDEAL_MAX_LINE_LEN - indent.length() - reserved;
        
        String denseStr = dense.write(node, indent);
        if (denseStr.length() <= availLength)
            return denseStr;
        
        return pretty.write(node, indent);
    }

    public String write(DataNode node) {
        return write(node, "", 0);
    }
}
