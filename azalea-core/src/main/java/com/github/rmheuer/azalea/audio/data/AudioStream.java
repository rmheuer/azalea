package com.github.rmheuer.azalea.audio.data;

import com.github.rmheuer.azalea.math.MathUtil;
import org.lwjgl.PointerBuffer;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayDeque;
import java.util.Queue;

import static org.lwjgl.stb.STBVorbis.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * An audio stream, decoding from an OGG file as it plays. This should be used
 * for longer audio files, such as music tracks, that you would not want fully
 * loaded into memory.
 */
public final class AudioStream implements AudioData {
    private static final int BUFFER_SIZE_INCREMENT = 1024;

    private final int bufferSize;
    private final int channels;
    private final int sampleRate;

    private final InputStream in;
    private final long vb;
    private final Queue<ShortBuffer> chunkQueue;
    private ShortBuffer currentFillingBuffer;

    private ByteBuffer buffer;
    private boolean hitEOF;

    /**
     * Internal use only
     *
     * @param in inputstream to read from
     * @throws IOException if IO error occurs
     */
    public AudioStream(InputStream in) throws IOException {
        this.in = in;
        chunkQueue = new ArrayDeque<>();

        buffer = MemoryUtil.memAlloc(BUFFER_SIZE_INCREMENT);
        buffer.position(0);
        buffer.limit(0);
        readMoreData(); // Initial buffer read

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer bytesConsumed = stack.mallocInt(1);
            IntBuffer error = stack.mallocInt(1);

            long vb;
            while (true) {
                vb = stb_vorbis_open_pushdata(buffer, bytesConsumed, error, null);
                if (vb != NULL) {
                    buffer.position(buffer.position() + bytesConsumed.get(0)); // Advance position
                    break; // Successfully read header, we can start streaming
                }

                int err = error.get(0);
                if (err != VORBIS_need_more_data) {
                    throw new IOException("Failed to decode OGG Vorbis header: " + vorbisErrToString(err));
                }

                if (!readMoreData())
                    throw new IOException("EOF while reading OGG Vorbis header");
            }
            this.vb = vb;

            STBVorbisInfo info = STBVorbisInfo.malloc(stack);
            stb_vorbis_get_info(vb, info);

            channels = info.channels();
            sampleRate = info.sample_rate();
            bufferSize = sampleRate * channels;

            currentFillingBuffer = MemoryUtil.memAllocShort(bufferSize);
        }

        hitEOF = false;
    }

    /**
     * Gets the number of audio channels within the stream. This will typically
     * be 1 for mono and 2 for stereo.
     *
     * @return number of channels
     */
    public int getChannels() {
        return channels;
    }

    /**
     * Gets the sample rate the audio data should be played at.
     *
     * @return sample rate in hz
     */
    public int getSampleRate() {
        return sampleRate;
    }

    // If position is at zero, makes a bigger buffer
    // Otherwise, moves the data to the start of the buffer
    private void makeMoreSpace() {
        boolean grow = buffer.position() == 0;

        ByteBuffer newBuffer = MemoryUtil.memAlloc(buffer.capacity() + (grow ? BUFFER_SIZE_INCREMENT : 0));
        newBuffer.put(buffer);
        MemoryUtil.memFree(buffer);
        newBuffer.flip();
        buffer = newBuffer;
    }

    private boolean readMoreData() throws IOException {
        // Make sure we have more space
        int remaining;
        while ((remaining = buffer.capacity() - buffer.limit()) == 0)
            makeMoreSpace();

        // Read in some data
        byte[] data = new byte[remaining];
        int read = in.read(data);
        if (read < 0)
            return false; // Return false if EOF

        // Buffer the data
        int positionBackup = buffer.position();
        int limit = buffer.limit();
        buffer.position(limit);
        buffer.limit(limit + read);
        buffer.put(data, 0, read);
        buffer.position(positionBackup);

        return true;
    }

    private short convertSampleFloatToShort(float sample) {
        return (short) MathUtil.clamp((int) (sample * 32767.5f - 0.5f), -32768, 32767);
    }

    /**
     * Gets the next block of samples in the stream. The buffer will contain
     * one second of audio data, unless the end is reached and less than a
     * second remains in the stream. The sample data from each channel is
     * interleaved per sample.
     *
     * @return next block of samples if available, otherwise null if the end is
     *         reached or an IO error occurs
     */
    public ShortBuffer readSamples() {
        if (hitEOF && chunkQueue.isEmpty() && currentFillingBuffer == null)
            return null;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer pSampleCount = stack.mallocInt(1);
            IntBuffer pChannelCount = stack.mallocInt(1);
            PointerBuffer pppOutput = stack.mallocPointer(1);

            enqueue:
            while (currentFillingBuffer != null && chunkQueue.isEmpty()) {
                while (true) {
                    int bytesUsed = stb_vorbis_decode_frame_pushdata(vb, buffer, pChannelCount, pppOutput, pSampleCount);
                    int err = stb_vorbis_get_error(vb);
                    if (err != VORBIS__no_error && err != VORBIS_need_more_data) {
                        throw new IOException("Failed to read OGG Vorbis frame: " + vorbisErrToString(err));
                    }

                    if (bytesUsed != 0) {
                        buffer.position(buffer.position() + bytesUsed);
                        break;
                    }

                    if (!readMoreData()) {
                        hitEOF = true;
                        break enqueue; // Reached end of stream
                    }
                }

                int sampleCount = pSampleCount.get(0);
                int channelCount = pChannelCount.get(0);

                if (channelCount == 0)
                    continue;

                PointerBuffer ppOutput = pppOutput.getPointerBuffer(0, channelCount);
                FloatBuffer[] channelData = new FloatBuffer[channelCount];
                for (int channel = 0; channel < channelCount; channel++) {
                    channelData[channel] = ppOutput.getFloatBuffer(channel, sampleCount);
                }

                for (int i = 0; i < sampleCount; i++) {
                    for (FloatBuffer channel : channelData) {
                        currentFillingBuffer.put(convertSampleFloatToShort(channel.get(i)));
                    }
                    if (!currentFillingBuffer.hasRemaining()) {
                        currentFillingBuffer.flip();
                        chunkQueue.add(currentFillingBuffer);
                        currentFillingBuffer = MemoryUtil.memAllocShort(bufferSize);
                    }
                }
            }

            // We have some leftover data
            if (hitEOF && currentFillingBuffer != null && currentFillingBuffer.position() != 0) {
                currentFillingBuffer.flip();
                chunkQueue.add(currentFillingBuffer);
                currentFillingBuffer = null;
            }
        } catch (IOException e) {
            System.err.println("Error streaming OGG Vorbis:");
            e.printStackTrace();
            return null;
        }

        return chunkQueue.poll();
    }

    @Override
    public void close() throws Exception {
        stb_vorbis_close(vb);

        in.close();
        MemoryUtil.memFree(buffer);

        // Free non-consumed buffers if the stream was interrupted
        if (currentFillingBuffer != null)
            MemoryUtil.memFree(currentFillingBuffer);
        for (ShortBuffer buf : chunkQueue)
            MemoryUtil.memFree(buf);
    }

    /**
     * Gets the string representation of an STB Vorbis error code.
     *
     * @param err STB Vorbis error code
     * @return error code name
     */
    public static String vorbisErrToString(int err) {
        switch (err) {
            case VORBIS__no_error:
                return "No error";
            case VORBIS_bad_packet_type:
                return "Bad packet type";
            case VORBIS_cant_find_last_page:
                return "Can't find last page";
            case VORBIS_continued_packet_flag_invalid:
                return "Continued packet flag invalid";
            case VORBIS_feature_not_supported:
                return "Feature not supported";
            case VORBIS_file_open_failure:
                return "File open failure";
            case VORBIS_incorrect_stream_serial_number:
                return "Incorrect stream serial number";
            case VORBIS_invalid_api_mixing:
                return "Invalid API mixing";
            case VORBIS_invalid_first_page:
                return "Invalid first page";
            case VORBIS_invalid_setup:
                return "Invalid setup";
            case VORBIS_invalid_stream:
                return "Invalid stream";
            case VORBIS_invalid_stream_structure_version:
                return "Invalid stream structure version";
            case VORBIS_missing_capture_pattern:
                return "Missing capture pattern";
            case VORBIS_ogg_skeleton_not_supported:
                return "OGG skeleton not supported";
            case VORBIS_outofmem:
                return "Out of memory";
            case VORBIS_seek_failed:
                return "Seek failed";
            case VORBIS_seek_invalid:
                return "Seek invalid";
            case VORBIS_seek_without_length:
                return "Seek without length";
            case VORBIS_too_many_channels:
                return "Too many channels";
            case VORBIS_unexpected_eof:
                return "Unexpected EOF";
            default:
                return "Unknown error (" + err + ")";
        }
    }
}
