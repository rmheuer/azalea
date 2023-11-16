package com.github.rmheuer.azalea.audio.play;

import com.github.rmheuer.azalea.audio.data.AudioStream;
import org.lwjgl.system.MemoryUtil;

import java.nio.ShortBuffer;

import static org.lwjgl.openal.AL10.*;

public final class PlayingStream extends PlayingSound {
    private static final int BUFFER_COUNT = 3;

    private final AudioStream stream;
    private final int[] buffers;
    private final int format;
    private boolean reachedEnd;

    public PlayingStream(int source, AudioStream stream) {
        super(source);
        this.stream = stream;
        reachedEnd = false;

        buffers = new int[BUFFER_COUNT];
        alGenBuffers(buffers);

        format = stream.getChannels() == 1 ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16;

        for (int i = 0; i < BUFFER_COUNT; i++) {
            ShortBuffer pcm = stream.readSamples();
            if (pcm == null) {
                reachedEnd = true;
                break;
            }
            alBufferData(buffers[i], format, pcm, stream.getSampleRate());
            alSourceQueueBuffers(source, buffers[i]);
            MemoryUtil.memFree(pcm);
        }

        alSourcePlay(source);
    }

    @Override
    public void setLooping(boolean looping) {
        throw new UnsupportedOperationException("TODO");
    }

    public void update() {
        if (reachedEnd)
            return;

        int processed = alGetSourcei(source, AL_BUFFERS_PROCESSED);
        for (int i = 0; i < processed; i++) {
            int buffer = alSourceUnqueueBuffers(source);

            ShortBuffer pcm = stream.readSamples();
            if (pcm == null) {
                reachedEnd = true;
                break;
            }
            alBufferData(buffer, format, pcm, stream.getSampleRate());
            MemoryUtil.memFree(pcm);
            alSourceQueueBuffers(source, buffer);
        }
    }

    @Override
    public int end() {
        alSourceStop(source);
        int[] ignored = new int[alGetSourcei(source, AL_BUFFERS_PROCESSED)];
        alSourceUnqueueBuffers(source, ignored);
        alDeleteBuffers(buffers);
        try {
            stream.close();
        } catch (Exception e) {
            System.err.println("Failed to close audio stream:");
            e.printStackTrace();
        }

        return super.end();
    }
}
