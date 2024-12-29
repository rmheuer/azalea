package com.github.rmheuer.azalea.voxel.level;

import com.github.rmheuer.azalea.utils.UnsafeUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FiniteVoxelLevel<B> implements VoxelLevel<B> {
    private final int width, height, depth;
    private final B[] blocks;
    private B outOfBounds;

    private final List<LevelListener<? super B>> listeners;

    public FiniteVoxelLevel(int width, int height, int depth, B initialBlock) {
        this(width, height, depth, UnsafeUtil.newGenericArray(width * height * depth));
        Arrays.fill(blocks, initialBlock);
    }

    public FiniteVoxelLevel(int width, int height, int depth, B[] initialContents) {
        if (initialContents.length != width * height * depth)
            throw new IllegalArgumentException("Initial contents was wrong size");

        this.width = width;
        this.height = height;
        this.depth = depth;
        blocks = initialContents;
        outOfBounds = null;

        listeners = new ArrayList<>();
    }

    private boolean isOutOfBounds(int x, int y, int z) {
        return x < 0 || x >= width || y < 0 || y >= height || z < 0 || z >= depth;
    }

    private int blockIndex(int x, int y, int z) {
        return x + z * width + y * width * depth;
    }

    @Override
    public B getBlock(int x, int y, int z) {
        if (isOutOfBounds(x, y, z))
            return outOfBounds;

        return blocks[blockIndex(x, y, z)];
    }

    @Override
    public B setBlock(int x, int y, int z, B block) {
        if (isOutOfBounds(x, y, z))
            return outOfBounds;

        int index = blockIndex(x, y, z);
        B previous = blocks[index];
        blocks[index] = block;

        if (previous != block) {
            for (LevelListener<? super B> listener : listeners) {
                listener.blockChanged(x, y, z, previous, block);
            }
        }

        return previous;
    }

    @Override
    public void addLevelListener(LevelListener<? super B> listener) {
        listeners.add(listener);
    }

    @Override
    public void removeLevelListener(LevelListener<? super B> listener) {
        listeners.remove(listener);
    }

    public void setOutOfBounds(B outOfBounds) {
        this.outOfBounds = outOfBounds;
    }
}
