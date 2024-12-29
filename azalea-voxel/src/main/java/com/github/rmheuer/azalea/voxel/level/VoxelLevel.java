package com.github.rmheuer.azalea.voxel.level;

public interface VoxelLevel<B> {
    B getBlock(int x, int y, int z);

    B setBlock(int x, int y, int z, B block);

    void addLevelListener(LevelListener<? super B> listener);

    void removeLevelListener(LevelListener<? super B> listener);
}
