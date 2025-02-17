package com.github.dialogos.plugin.file.input;

import com.clt.dialogos.plugin.AudioPlugin;
import com.clt.dialogos.plugin.PluginSettings;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;

/**
 * Plugin only used for testing purposes. Plays audio from a set file
 */
public class FileAudioInputPlugin implements AudioPlugin {

    AudioInputStream inputStream;

    @Override
    public boolean isAudioInputPlugin() {
        return true;
    }

    @Override
    public boolean isAudioOutputPlugin() {
        return false;
    }

    @Override
    public String getId() {
        return "dialogos.plugin.fileAudioInput";
    }

    @Override
    public String getName() {
        return "Datei Audioeingabe";
    }

    @Override
    public Icon getIcon() {
        return null;
    }

    @Override
    public String getVersion() {
        return "1";
    }

    @Override
    public void initialize() {}

    @Override
    public PluginSettings createDefaultSettings() {
        return new Settings();
    }

    @Override
    public boolean isRecording() {
        return false;
    }

    @Override
    public InputStream setupAndGetAudioInput() throws IOException {
        return inputStream;
    }

    @Override
    public void stopRecording() {
    }

    @Override
    public void startRecording() {
    }

    @Override
    public void setInputStream(InputStream inputStream) {
        try {
            this.inputStream = AudioSystem.getAudioInputStream(inputStream);;
        } catch (UnsupportedAudioFileException | IOException ex) {
            ex.printStackTrace();
        }
    }
}
