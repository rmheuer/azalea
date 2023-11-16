package com.github.rmheuer.azalea.audio.play;

import org.joml.Vector3fc;

import static org.lwjgl.openal.AL10.*;

public abstract class PlayingSound {
    protected final int source;
    protected volatile boolean finished;

    public PlayingSound(int source) {
        this.source = source;
        finished = false;
    }

    public void setPositionAbsolute(Vector3fc pos) {
        if (finished) return;
        alSourcei(source, AL_SOURCE_RELATIVE, AL_FALSE);
        alSource3f(source, AL_POSITION, pos.x(), pos.y(), pos.z());
    }

    public void setPositionRelative(Vector3fc pos) {
        if (finished) return;
        alSourcei(source, AL_SOURCE_RELATIVE, AL_TRUE);
        alSource3f(source, AL_POSITION, pos.x(), pos.y(), pos.z());
    }

    public void setGain(float gain) {
        if (finished) return;
        alSourcef(source, AL_GAIN, gain);
    }

    public void setPitch(float pitch) {
        if (finished) return;
        alSourcef(source, AL_PITCH, pitch);
    }

    public abstract void setLooping(boolean looping);

    public boolean isPlaying() {
        return !finished && alGetSourcei(source, AL_SOURCE_STATE) == AL_PLAYING;
    }

    public void stop() {
        if (finished) return;
        alSourceStop(source);
    }

    public int end() {
        if (finished) throw new IllegalStateException("Already finished");
        finished = true;
        alSourcei(source, AL_BUFFER, 0);
        return source;
    }
}
