package com.github.rmheuer.azalea.audio;

import com.github.rmheuer.azalea.audio.data.AudioData;
import com.github.rmheuer.azalea.audio.data.AudioSample;
import com.github.rmheuer.azalea.audio.data.AudioStream;
import com.github.rmheuer.azalea.audio.play.DummySound;
import com.github.rmheuer.azalea.audio.play.PlayingSample;
import com.github.rmheuer.azalea.audio.play.PlayingSound;
import com.github.rmheuer.azalea.audio.play.PlayingStream;
import com.github.rmheuer.azalea.math.PoseStack;
import com.github.rmheuer.azalea.math.Transform;
import org.joml.Vector3fc;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.concurrent.atomic.AtomicInteger;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;

/**
 * The main audio system.
 */
public final class AudioSystem implements AutoCloseable {
    private static final int MAX_SOURCES = 255;

    private final long device;
    private final long context;
    private final int[] sourcePool;
    private final AtomicInteger poolIdx;
    private final AudioThread thread;

    /**
     * Creates a new audio system. This creates a new thread which
     * will be stopped when {@link #close()} is called.
     */
    public AudioSystem() {
        device = alcOpenDevice((ByteBuffer) null);
        ALCCapabilities deviceCaps = ALC.createCapabilities(device);

        context = alcCreateContext(device, (IntBuffer) null);
        alcMakeContextCurrent(context);
        AL.createCapabilities(deviceCaps);

        sourcePool = new int[MAX_SOURCES];
        alGenSources(sourcePool);
        poolIdx = new AtomicInteger(MAX_SOURCES - 1);

        thread = new AudioThread(this);
        thread.start();
    }

    /**
     * Loads an {@code AudioSample} from an {@code InputStream}. The audio
     * system will take ownership of the input stream, so you do not need to
     * close it after calling this.
     *
     * @param in InputStream to read from
     * @return loaded sample
     * @throws IOException if an IO error occurs while reading the stream
     */
    public AudioSample createSample(InputStream in) throws IOException {
        return new AudioSample(in);
    }

    /**
     * Creates an {@code AudioStream} that reads from an {@code InputStream}.
     * The audio system will keep the input stream open until the stream
     * finishes playing, so do not close the stream after calling this.
     *
     * @param in InputStream to read from
     * @return loaded stream
     * @throws IOException if an IO error occurs while initializing the stream
     */
    public AudioStream createStream(InputStream in) throws IOException {
        return new AudioStream(in);
    }

    private int getSource() {
        int idx = poolIdx.getAndUpdate((i) -> i < 0 ? i : i - 1);
        if (idx >= 0)
            return sourcePool[idx];
        else
            return -1; // Out of sources
    }

    // Returns a source to the source pool
    void returnSource(int id) {
        if (id != -1)
            sourcePool[poolIdx.incrementAndGet()] = id;
    }

    /**
     * Begins playback of a sound.
     *
     * @param options the settings for source playback
     * @return reference to the playing sound
     */
    public PlayingSound play(PlayOptions options) {
        int source = getSource();
        if (source == -1)
            return new DummySound();

        Vector3fc pos = options.getPosition();
        alSourcei(source, AL_SOURCE_RELATIVE, options.getMode() == SpatialMode.RELATIVE ? AL_TRUE : AL_FALSE);
        alSource3f(source, AL_POSITION, pos.x(), pos.y(), pos.z());
        alSourcef(source, AL_PITCH, options.getPitch());
        alSourcef(source, AL_GAIN, options.getGain());
        alSourcei(source, AL_LOOPING, options.isLooping() ? AL_TRUE : AL_FALSE);

        AudioData data = options.getData();
        PlayingSound playing = null;
        if (data instanceof AudioSample) {
            AudioSample sample = (AudioSample) data;
            alSourcei(source, AL_BUFFER, sample.getBuffer());
            alSourcePlay(source);
            playing = new PlayingSample(source);
        } else if (data instanceof AudioStream) {
            AudioStream stream = (AudioStream) data;
            playing = new PlayingStream(source, stream);
        }

        thread.add(playing);
        return playing;
    }

    /**
     * Sets the position of the listener in world space.
     *
     * @param pos new listener position
     */
    public void setListenerPosition(Vector3fc pos) {
        alListener3f(AL_POSITION, pos.x(), pos.y(), pos.z());
    }

    /**
     * Sets the orientation of the listener in world space. The forward and up
     * vectors should be perpendicular, but do not need to be normalized.
     *
     * @param forward forward direction vector
     * @param up up direction vector
     */
    public void setListenerOrientation(Vector3fc forward, Vector3fc up) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buf = stack.floats(
                    forward.x(), forward.y(), forward.z(),
                    up.x(), up.y(), up.z()
            );
            alListenerfv(AL_ORIENTATION, buf);
        }
    }

    /**
     * Sets the listener position from a {@code Transform}. This is equivalent
     * to using {@link #setListenerPosition(Vector3fc)} and
     * {@link #setListenerOrientation(Vector3fc, Vector3fc)} individually.
     *
     * @param tx listener transform
     */
    public void setListenerTransform(Transform tx) {
        setListenerPosition(tx.position);
        setListenerOrientation(tx.getForward(), tx.getUp());
    }

    /**
     * Sets the listener position from a {@code PoseStack}. This is equivalent
     * to using {@link #setListenerPosition(Vector3fc)} and
     * {@link #setListenerOrientation(Vector3fc, Vector3fc)} individually.
     *
     * @param pose listener pose
     */
    public void setListenerPose(PoseStack pose) {
        setListenerPosition(pose.getPosition());
        setListenerOrientation(pose.getForward(), pose.getUp());
    }

    /**
     * Sets the listener's velocity in world space. This is used to simulate
     * the Doppler effect.
     *
     * @param vel listener velocity in units/sec
     */
    public void setListenerVelocity(Vector3fc vel) {
        alListener3f(AL_VELOCITY, vel.x(), vel.y(), vel.z());
    }

    /**
     * Sets the overall gain for all audio.
     * @param gain new gain, 1 is default volume.
     */
    public void setListenerGain(float gain) {
        alListenerf(AL_GAIN, gain);
    }

    @Override
    public void close() {
        try {
            thread.end();
            thread.join(1000);
        } catch (InterruptedException e) {}
        alcDestroyContext(context);
        alcCloseDevice(device);
    }
}
