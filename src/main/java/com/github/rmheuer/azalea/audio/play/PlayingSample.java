package com.github.rmheuer.azalea.audio.play;

import org.lwjgl.openal.AL10;

public final class PlayingSample extends PlayingSound {
    public PlayingSample(int source) {
        super(source);
    }

    @Override
    public void setLooping(boolean looping) {
        if (finished) return;
        AL10.alSourcei(source, AL10.AL_LOOPING, looping ? AL10.AL_TRUE : AL10.AL_FALSE);
    }
}
