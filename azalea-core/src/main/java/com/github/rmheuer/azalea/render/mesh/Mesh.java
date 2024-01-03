package com.github.rmheuer.azalea.render.mesh;

import com.github.rmheuer.azalea.utils.SafeCloseable;

/**
 * A mesh stored on the GPU.
 */
public interface Mesh extends SafeCloseable {
    /**
     * The intended usage of the uploaded data. This helps select the most
     * optimal video memory to use for the data.
     */
    enum DataUsage {
        /** The data is uploaded rarely and rendered many times */
        STATIC,

        /** The data is uploaded frequently and rendered many times */
        DYNAMIC,

        /** The data is uploaded frequently and rendered once */
        STREAM
    }

    /**
     * Uploads a new set of data to the GPU.
     *
     * @param data new mesh data
     * @param usage intended usage of the data
     */
    void setData(MeshData data, DataUsage usage);

    /**
     * Gets whether this mesh has data on the GPU.
     *
     * @return has data
     */
    boolean hasData();
}
