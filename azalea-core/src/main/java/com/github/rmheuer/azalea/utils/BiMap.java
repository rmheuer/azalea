package com.github.rmheuer.azalea.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Bidirectional map. Each key maps to a value, and the value maps back to the
 * key.
 *
 * @param <A> value A type
 * @param <B> value B type
 */
public final class BiMap<A, B> {
    private final Map<A, B> aToB;
    private final Map<B, A> bToA;

    /**
     * Creates a new empty {@code BiMap}.
     */
    public BiMap() {
        aToB = new HashMap<>();
        bToA = new HashMap<>();
    }

    /**
     * Puts a pair of values into the map.
     *
     * @param a value A
     * @param b value B
     */
    public void put(A a, B b) {
        aToB.put(a, b);
        bToA.put(b, a);
    }

    /**
     * Gets the {@code A} that corresponds to a provided {@code B}.
     *
     * @param b B to look up
     * @return A that corresponds
     */
    public A getA(B b) {
        return bToA.get(b);
    }

    /**
     * Gets the {@code B} that corresponds to a provided {@code A}.
     *
     * @param a {@code A} to look up
     * @return {@code B} that corresponds
     */
    public B getB(A a) {
        return aToB.get(a);
    }

    /**
     * Gets the {@code A} that corresponds to a provided {@code B}, or returns
     * a default if not found.
     *
     * @param b {@code B} to look up
     * @param def default {@code A} to return if mapping is not found
     * @return {@code A} that corresponds, or {@code def} if mapping not found
     */
    public A getAOrDefault(B b, A def) {
        return bToA.getOrDefault(b, def);
    }

    /**
     * Gets the {@code B} that corresponds to a provided {@code A}, or returns
     * a default if not found.
     *
     * @param a {@code A} to look up
     * @param def default {@code B} to return if mapping is not found
     * @return {@code B} that corresponds, or {@code def} if mapping not found
     */
    public B getBOrDefault(A a, B def) {
        return aToB.getOrDefault(a, def);
    }

    /**
     * Gets the set of all {@code A} values in the map.
     *
     * @return all {@code A} values
     */
    public Set<A> getAllA() {
        return aToB.keySet();
    }

    /**
     * Gets the set of all {@code B} values in the map.
     *
     * @return all {@code B} values
     */
    public Set<B> getAllB() {
        return bToA.keySet();
    }

    /**
     * Gets the number of entries in the map.
     *
     * @return entry count
     */
    public int size() {
        return aToB.size();
    }

    /**
     * Clears the map. It will no longer contain any values.
     */
    public void clear() {
        aToB.clear();
        bToA.clear();
    }
}
