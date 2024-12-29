package com.github.rmheuer.azalea.voxel.level;

public interface LevelListener<B> {
    void blockChanged(int x, int y, int z, B prevBlock, B newBlock);
}
