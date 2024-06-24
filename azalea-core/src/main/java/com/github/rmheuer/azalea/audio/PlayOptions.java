package com.github.rmheuer.azalea.audio;

import com.github.rmheuer.azalea.audio.data.AudioData;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public final class PlayOptions {
    /**
     * Plays a sound in 2D (i.e. play it directly to the listener without 3D
     * effects applied).
     *
     * @param data audio data to play
     * @return options
     */
    public static PlayOptions play2D(AudioData data) {
        return new PlayOptions(data, SpatialMode.RELATIVE);
    }

    /**
     * Plays a sound in 3D (i.e. play from a point in 3D space).
     *
     * @param data audio data to play
     * @return options
     */
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

    /**
     * Gets the AudioData to play.
     *
     * @return data
     */
    public AudioData getData() {
        return data;
    }

    /**
     * Gets the reference frame for the position.
     *
     * @return spatial mode
     */
    public SpatialMode getMode() {
        return mode;
    }

    /**
     * Gets the position to play the sound from.
     *
     * @return source position
     */
    public Vector3fc getPosition() {
        return position;
    }

    /**
     * Sets the position to play the sound from.
     *
     * @param position new position
     * @return this
     */
    public PlayOptions setPosition(Vector3fc position) {
        this.position = position;
        return this;
    }

    /**
     * Gets the pitch to play the sound at.
     *
     * @return pitch
     */
    public float getPitch() {
        return pitch;
    }

    /**
     * Sets the pitch of the audio. A pitch of 1 is the default pitch, with
     * higher values corresponding to higher pitch. A pitch of 2 would be
     * an an octave higher pitch.
     *
     * @param pitch new pitch
     * @return this
     */
    public PlayOptions setPitch(float pitch) {
        this.pitch = pitch;
        return this;
    }

    /**
     * Gets the gain to play the sound at.
     *
     * @return gain
     */
    public float getGain() {
        return gain;
    }

    /**
     * Sets the gain of the audio. A gain of 1 is the default volume, with
     * higher values corresponding to louder sound.
     *
     * @param gain new gain
     * @return this
     */
    public PlayOptions setGain(float gain) {
        this.gain = gain;
        return this;
    }

    /**
     * Gets whether the sound should repeat when it reaches the end.
     *
     * @return looping
     */
    public boolean isLooping() {
        return looping;
    }

    /**
     * Sets whether the audio should automatically repeat when it reaches the
     * end.
     *
     * @param looping whether to loop
     * @return this
     */
    public PlayOptions setLooping(boolean looping) {
        this.looping = looping;
        return this;
    }
}
