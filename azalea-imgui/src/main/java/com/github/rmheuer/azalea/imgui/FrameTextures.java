package com.github.rmheuer.azalea.imgui;

import com.github.rmheuer.azalea.render.texture.Texture2D;

import java.util.ArrayList;
import java.util.List;

public final class FrameTextures {
    private final List<Texture2D> frameTextures;

    public FrameTextures() {
        frameTextures = new ArrayList<>();
    }

    public void newFrame() {
        frameTextures.clear();
    }

    public int getIdForTexture(Texture2D tex) {
        int index = frameTextures.indexOf(tex);
        if (index > 0)
            return index;

        index = frameTextures.size();
        frameTextures.add(tex);
        return index;
    }

    public Texture2D getTexture(int id) {
        return frameTextures.get(id);
    }
}
