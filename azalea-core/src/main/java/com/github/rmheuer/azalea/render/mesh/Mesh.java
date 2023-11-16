package com.github.rmheuer.azalea.render.mesh;

import com.github.rmheuer.azalea.utils.SafeCloseable;

public interface Mesh extends SafeCloseable {
    enum DataUsage {
        STATIC,
        DYNAMIC,
        STREAM
    }

    void setData(MeshData data, DataUsage usage);
    boolean hasData();
}
