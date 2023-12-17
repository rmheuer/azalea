package com.github.rmheuer.azalea.utils;

import java.util.HashMap;
import java.util.Map;

public final class BiMap<A, B> {
    private final Map<A, B> aToB;
    private final Map<B, A> bToA;

    public BiMap() {
        aToB = new HashMap<>();
        bToA = new HashMap<>();
    }

    public void put(A a, B b) {
        aToB.put(a, b);
        bToA.put(b, a);
    }

    public A getA(B b) {
        return bToA.get(b);
    }

    public B getB(A a) {
        return aToB.get(a);
    }

    public A getAOrDefault(B b, A def) {
        return bToA.getOrDefault(b, def);
    }

    public B getBOrDefault(A a, B def) {
        return aToB.getOrDefault(a, def);
    }

    public int size() {
        return aToB.size();
    }

    public void clear() {
        aToB.clear();
        bToA.clear();
    }
}
