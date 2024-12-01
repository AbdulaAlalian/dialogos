package com.clt.dialogos.plugin;

import java.io.IOException;
import java.io.InputStream;

public interface AudioPlugin extends Plugin {
    boolean isAudioInputPlugin();

    boolean isAudioOutputPlugin();

    /**
     * Starts the recording of audio from an Audiostream. This is implemented by an audioInput plugin and the default implementation of startRecording
     * does nothing.
     */
    default void startRecording() {

    }

    /**
     * Stops the recording of audio from an Audiostream. This is implemented by an audioInput plugin and the default implementation of stopRecording
     * does nothing.
     */
    default void stopRecording() {

    }

    /**
     * Returns true if the AudioPlugin is recording. This is implemented by an audioInput plugin and the
     * default implementation of isRecording returns false.
     * @return true if the AudioPlugin is recording, false otherwise
     */
    default boolean isRecording() {
        return false;
    }

    /**
     * Prepares and returns the audio input stream which receives audio data from an audio
     * capturing device (e.g. microphone). This is implemented by an Audioinput plugin and the
     * default implementation of setupAndGetAudioInput returns null
     * @return the Inputstream representing the input of an audio capturing device
     * @throws IOException
     */
    default InputStream setupAndGetAudioInput() throws IOException {
        return null;
    }
}
