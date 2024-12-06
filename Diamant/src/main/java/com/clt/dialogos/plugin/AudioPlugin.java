package com.clt.dialogos.plugin;

import javax.sound.sampled.AudioInputStream;
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

    /**
     * Plays the audio from the specified audio inputstream (e.g. synthesised audio from maryTTS) to an audio output device.
     * This method is implemented by an Audiooutput plugin and the default implementation of playAudio does nothing.
     */
    default void playAudio(AudioInputStream audioInputStream) {};

    /**
     * Stops the playing of Audio to an audio output device. This Method is implemented by an audiooutput plugin and the
     * default implementation of stopAudio does nothing.
     */
    default void stopAudio() {}

    /**
     * Calls the join method of the thread, responsible for playing the audio to the audio output device
     */
    default void joinAudioOutputThread() throws InterruptedException {}
}
