package com.github.rmheuer.azalea.utils;

import java.util.Iterator;
import java.util.function.Function;

/**
 * Various utilities for iterators
 */
public final class IteratorUtil {
    /**
     * Applies a function to each element in an iterator.
     *
     * @param in input iterator
     * @param mappingFn function to apply
     * @param <I> input element type
     * @param <O> output element type
     * @return output iterator
     */
    public static <I, O> Iterator<O> map(Iterator<I> in, Function<I, O> mappingFn) {
        return new Iterator<O>() {
            @Override
            public boolean hasNext() {
                return in.hasNext();
            }

            @Override
            public O next() {
                return mappingFn.apply(in.next());
            }
        };
    }

    private IteratorUtil() {
        throw new AssertionError();
    }
}
