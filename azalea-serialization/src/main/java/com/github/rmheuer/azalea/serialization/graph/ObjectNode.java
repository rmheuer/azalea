package com.github.rmheuer.azalea.serialization.graph;

import com.github.rmheuer.azalea.utils.StringUtil;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public final class ObjectNode implements DataNode {
    private final Map<String, DataNode> values;

    public ObjectNode() {
        values = new LinkedHashMap<>();
    }

    public int size() {
        return values.size();
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public boolean has(String key) {
        return values.containsKey(key);
    }

    public DataNode get(String key) {
        return values.get(key);
    }

    public DataNode put(String key, DataNode dataNode) {
        return values.put(key, dataNode);
    }

    public DataNode remove(String key) {
        return values.remove(key);
    }

    public void clear() {
        values.clear();
    }

    public Set<String> keySet() {
        return values.keySet();
    }

    public Collection<DataNode> values() {
        return values.values();
    }

    public Map<String, DataNode> getValues() {
        return values;
    }

    @Override
    public String toString() {
        return StringUtil.mapToString(values);
    }
}
