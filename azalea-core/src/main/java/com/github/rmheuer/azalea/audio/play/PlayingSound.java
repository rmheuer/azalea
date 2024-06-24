package com.github.rmheuer.azalea.audio.play;

import org.joml.Vector3fc;

import static org.lwjgl.openal.AL10.*;

/**
 * Provides access to a sound that is currently playing. This allows changing
 * the sound properties while it plays.
 */
public abstract class PlayingSound {
    protected final int source;
    protected volatile boolean finished;

    /**
     * Internal use only.
     *
     * @param source OpenAL source name
     */
    public PlayingSound(int source) {
        this.source = source;
        finished = false;
    }

    /**
     * Sets the source's position in world space.
     *
     * @param pos absolute position in world space
     */
    public void setPositionAbsolute(Vector3fc pos) {
        if (finished) return;
        alSourcei(source, AL_SOURCE_RELATIVE, AL_FALSE);
        alSource3f(source, AL_POSITION, pos.x(), pos.y(), pos.z());
    }

    /**
     * Sets the source's position relative to the listener.
     *
     * @param pos position relative to the listener
     */
    public void setPositionRelative(Vector3fc pos) {
        if (finished) return;
        alSourcei(source, AL_SOURCE_RELATIVE, AL_TRUE);
        alSource3f(source, AL_POSITION, pos.x(), pos.y(), pos.z());
    }

    /**
     * Sets the gain of the audio. A gain of 1 is the default volume, with
     * higher values corresponding to louder sound.
     *
     * @param gain new gain
     */
    public void setGain(float gain) {
        if (finished) return;
        alSourcef(source, AL_GAIN, gain);
    }

    /**
     * Sets the pitch of the audio. A pitch of 1 is the default pitch, with
     * higher values corresponding to higher pitch. A pitch of 2 would be
     * an an octave higher pitch.
     *
     * @param pitch new pitch
     */
    public void setPitch(float pitch) {
        if (finished) return;
        alSourcef(source, AL_PITCH, pitch);
    }

    /**
     * Sets whether the audio should automatically repeat when it reaches the
     * end.
     *
     * @param looping whether to loop
     */
    public abstract void setLooping(boolean looping);

    /**
     * Gets whether the audio is currently playing.
     *
     * @return if playing
     */
    public boolean isPlaying() {
        return !finished && alGetSourcei(source, AL_SOURCE_STATE) == AL_PLAYING;
    }

    /**
     * Stops the audio playback immediately.
     */
    public void stop() {
        if (finished) return;
        alSourceStop(source);
    }

    /**
     * Internal use only.
     * Releases the OpenAL source and any buffers associated with this sound.
     *
     * @return OpenAL source name
     */
    public int end() {
        if (finished) throw new IllegalStateException("Already finished");
        finished = true;
        alSourcei(source, AL_BUFFER, 0);
        return source;
    }
}
