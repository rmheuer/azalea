package com.github.rmheuer.azalea.audio;

import com.github.rmheuer.azalea.audio.data.AudioData;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public final class PlayOptions {
    public static PlayOptions play2D(AudioData data) {
        return new PlayOptions(data, SpatialMode.RELATIVE);
    }

    public static PlayOptions play3D(AudioData data, Vector3fc position) {
        return new PlayOptions(data, SpatialMode.ABSOLUTE).setPosition(position);
    }

    private final AudioData data;
    private final SpatialMode mode;
    private Vector3fc position;
    private float pitch;
    private float gain;
    private boolean looping;

    private PlayOptions(AudioData data, SpatialMode mode) {
        this.data = data;
        this.mode = mode;
        position = new Vector3f(0, 0, 0);
        pitch = 1;
        gain = 1;
        looping = false;
    }

    public AudioData getData() {
        return data;
    }

    public SpatialMode getMode() {
        return mode;
    }

    public Vector3fc getPosition() {
        return position;
    }

    public PlayOptions setPosition(Vector3fc position) {
        this.position = position;
        return this;
    }

    public float getPitch() {
        return pitch;
    }

    public PlayOptions setPitch(float pitch) {
        this.pitch = pitch;
        return this;
    }

    public float getGain() {
        return gain;
    }

    public PlayOptions setGain(float gain) {
        this.gain = gain;
        return this;
    }

    public boolean isLooping() {
        return looping;
    }

    public PlayOptions setLooping(boolean looping) {
        this.looping = looping;
        return this;
    }
}
