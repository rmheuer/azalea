package com.github.rmheuer.azalea.audio.data;

import com.github.rmheuer.azalea.io.IOUtil;
import com.github.rmheuer.azalea.utils.SizeOf;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.stb.STBVorbis.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * An audio sample, loaded fully into memory. This should be used for relatively
 * short samples so you don't have as much data loaded into memory.
 */
public final class AudioSample implements AudioData {
    private final int buffer;
    private final long sizeInBytes;

    /**
     * Internal use only
     *
     * @param in inputstream to read from
     * @throws IOException if IO error occurs
     */
    public AudioSample(InputStream in) throws IOException {
        ByteBuffer ogg = IOUtil.readToByteBuffer(in);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer err = stack.mallocInt(1);
            long vb = stb_vorbis_open_memory(ogg, err, null);
            if (vb == NULL) {
                throw new IOException("Failed to open OGG file: " + AudioStream.vorbisErrToString(err.get(0)));
            }

            STBVorbisInfo info = STBVorbisInfo.malloc(stack);
            stb_vorbis_get_info(vb, info);

            int samples = stb_vorbis_stream_length_in_samples(vb);
            int channels = info.channels();

            ShortBuffer pcm = MemoryUtil.memAllocShort(samples * channels);
            pcm.limit(stb_vorbis_get_samples_short_interleaved(vb, channels, pcm) * channels);
	    stb_vorbis_close(vb);

	    sizeInBytes = pcm.limit() * SizeOf.SHORT;

            buffer = alGenBuffers();
            alBufferData(buffer, channels == 1 ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16, pcm, info.sample_rate());

            MemoryUtil.memFree(pcm);
            MemoryUtil.memFree(ogg);
        }
    }

    /**
     * Gets the OpenAL buffer name this data is stored in
     *
     * @return OpenAL buffer name
     */
    public int getBuffer() {
        return buffer;
    }

    /**
     * Gets the size of the audio data stored in this buffer in bytes.
     * This data is stored off-heap in native memory.
     */
    public long getSizeInBytes() {
	return sizeInBytes;
    }

    @Override
    public void close() {
        alDeleteBuffers(buffer);
    }
}
