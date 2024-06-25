package com.github.rmheuer.azalea.serialization.graph;

import com.github.rmheuer.azalea.utils.StringUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class ArrayNode implements DataNode, Iterable<DataNode> {
    private final List<DataNode> values;

    public ArrayNode() {
        this.values = new ArrayList<>();
    }

    public void add(DataNode node) {
        values.add(node);
    }

    public void insert(int index, DataNode node) {
        values.add(index, node);
    }

    public DataNode remove(int index) {
        return values.remove(index);
    }

    public boolean remove(DataNode node) {
        return values.remove(node);
    }

    public void clear() {
        values.clear();
    }

    public DataNode get(int index) {
        return values.get(index);
    }

    public DataNode set(int index, DataNode dataNode) {
        return values.set(index, dataNode);
    }

    public List<DataNode> getValues() {
        return values;
    }

    public int size() {
        return values.size();
    }

    @Override
    public Iterator<DataNode> iterator() {
        return values.iterator();
    }

    @Override
    public String toString() {
        return StringUtil.iterableToString(values);
    }
}
