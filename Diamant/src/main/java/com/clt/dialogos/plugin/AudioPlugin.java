package com.clt.dialogos.plugin;

import java.io.IOException;
import java.io.InputStream;

public interface AudioPlugin extends Plugin {
    boolean isAudioInputPlugin();

    boolean isAudioOutputPlugin();

    void startRecording();

    void stopRecording();

    boolean isRecording();

    /**
     * Prepares and returns the audio input stream which receives audio data from an audio
     * capturing device (e.g. microphone)
     * @return the Inputstream representing the input of an audio capturing device
     * @throws IOException
     */
    InputStream setupAndGetAudioInput() throws IOException;
}
