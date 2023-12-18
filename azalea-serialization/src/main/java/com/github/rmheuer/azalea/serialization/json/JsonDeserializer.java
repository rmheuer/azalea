package com.github.rmheuer.azalea.serialization.json;

import com.github.rmheuer.azalea.serialization.graph.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class JsonDeserializer {
    private static final Pattern NUMBER_PATTERN = Pattern.compile("^-?(?:(?:\\d+)(?:\\.\\d+)?(?:[Ee][+-]?\\d+)?|Infinity|NaN)");

    private static final class Input {
        private final String str;
        private final char[] chars;
        private int index;
        private boolean shouldSkipWhitespace;

        public Input(String str) {
            this.str = str;
            this.chars = str.toCharArray();
            index = 0;
            shouldSkipWhitespace = true;
        }

        private void skipWhitespace() {
            if (!shouldSkipWhitespace)
                return;

            while (index < chars.length && Character.isWhitespace(chars[index]))
                index++;
        }

        public char next() {
            skipWhitespace();
            if (atEnd())
                throw new JsonParseException("Unexpected EOF");

            return chars[index++];
        }

        public char peek() {
            skipWhitespace();
            if (atEnd())
                throw new JsonParseException("Unexpected EOF");

            return chars[index];
        }

        public boolean atEnd() {
            return index >= chars.length;
        }

        public void expect(char c) {
            char n = next();
            if (n != c)
                throw new JsonParseException("Expected '" + c + "', found '" + n + "'");
        }

        public void expect(String str) {
            // TODO: Do this better so the error message can be more helpful
            for (char c : str.toCharArray()) {
                expect(c);
            }
        }

        public String nextMatching(Pattern pattern) {
            Matcher m = pattern.matcher(str.substring(index));
            if (!m.find())
                throw new JsonParseException("Expected number, found: " + str.substring(index, Math.min(index + 16, str.length())));
            String matched = str.substring(index + m.start(), index + m.end());
            index += m.end();
            return matched;
        }

        public void setShouldSkipWhitespace(boolean shouldSkipWhitespace) {
            this.shouldSkipWhitespace = shouldSkipWhitespace;
        }
    }

    private final Input in;

    public JsonDeserializer(String json) {
        in = new Input(json);
    }

    private int fromHex(char c) {
        if (c >= '0' && c <= '9') return c - '0';
        if (c >= 'a' && c <= 'f') return c - 'a' + 10;
        if (c >= 'A' && c <= 'F') return c - 'A' + 10;
        throw new JsonParseException("Invalid hex char: '" + c + "'");
    }

    private char fromHex(char a, char b, char c, char d) {
        int out = fromHex(d);
        out |= fromHex(c) << 4;
        out |= fromHex(b) << 8;
        out |= fromHex(a) << 12;
        return (char) out;
    }

    private String readString() {
        in.expect('"');
        in.setShouldSkipWhitespace(false);
        StringBuilder builder = new StringBuilder();

        while (in.peek() != '"') {
            char c = in.next();

            if (c == '\\') {
                char escape = in.next();
                switch (escape) {
                    case '"': c = '"'; break;
                    case '\\': c = '\\'; break;
                    case '/': c = '/'; break;
                    case 'b': c = '\b'; break;
                    case 'f': c = '\f'; break;
                    case 'n': c = '\n'; break;
                    case 'r': c = '\r'; break;
                    case 't': c = '\t'; break;
                    case 'u': c = fromHex(in.next(), in.next(), in.next(), in.next()); break;
                    default:
                        throw new JsonParseException("Invalid escape char: '" + escape + "'");
                }
            }

            builder.append(c);
        }

        in.expect(  '"');
        in.setShouldSkipWhitespace(true);
        return builder.toString();
    }

    private ObjectNode deserializeObject() {
        ObjectNode obj = new ObjectNode();
        in.expect('{');
        while (in.peek() != '}') {
            String key = readString();
            in.expect(':');
            DataNode value = deserializeNode();

            obj.put(key, value);

            if (in.peek() != '}')
                in.expect(',');
        }
        in.expect('}');
        return obj;
    }

    private ArrayNode deserializeArray() {
        ArrayNode arr = new ArrayNode();
        in.expect('[');
        while (in.peek() != ']') {
            DataNode value = deserializeNode();
            arr.add(value);

            if (in.peek() != ']')
                in.expect(',');
        }
        in.expect(']');
        return arr;
    }

    private StringNode deserializeString() {
        return new StringNode(readString());
    }

    private NumberNode deserializeNumber() {
        String str = in.nextMatching(NUMBER_PATTERN);
        // Decimal, exponent, I (Infinity), or N (NaN)
        if (str.contains(".") || str.contains("e") || str.contains("E") || str.contains("I") || str.contains("N")) {
            return new DoubleNode(Double.parseDouble(str));
        } else {
            return new LongNode(Long.parseLong(str));
        }
    }

    public DataNode deserializeNode() {
        switch (in.peek()) {
            case '{': return deserializeObject();
            case '[': return deserializeArray();
            case '"': return deserializeString();
            case 't':
                in.expect("true");
                return new BooleanNode(true);
            case 'f':
                in.expect("false");
                return new BooleanNode(false);
            case 'n':
                in.expect("null");
                return NullNode.INSTANCE;
            default:
                return deserializeNumber();
        }
    }
}
