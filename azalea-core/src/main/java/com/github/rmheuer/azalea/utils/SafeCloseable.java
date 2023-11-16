package com.github.rmheuer.azalea.utils;

public interface SafeCloseable extends AutoCloseable {
    @Override
    void close();
}
