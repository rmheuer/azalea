package com.github.rmheuer.azalea.audio.data;

/**
 * Represents a source of audio data (i.e. an audio file). This should only
 * be implemented by {@link AudioSample} and {@link AudioStream}, no other
 * data is supported by the audio system.
 */
// TODO: Could we make it possible to have custom data sources?
public interface AudioData extends AutoCloseable {
}
