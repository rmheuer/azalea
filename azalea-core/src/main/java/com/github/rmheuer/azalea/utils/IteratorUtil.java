package com.github.rmheuer.azalea.utils;

import java.util.Iterator;
import java.util.function.Function;

public final class IteratorUtil {
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
