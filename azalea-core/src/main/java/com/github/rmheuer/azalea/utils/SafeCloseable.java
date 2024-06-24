package com.github.rmheuer.azalea.utils;

/**
 * Able to be closed without throwing an exception.
 */
public interface SafeCloseable extends AutoCloseable {
    @Override
    void close();
}
