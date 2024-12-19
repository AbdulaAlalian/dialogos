package com.github.dialogos.plugin.local.output;

import com.clt.dialogos.plugin.AudioPlugin;
import com.clt.dialogos.plugin.PluginSettings;

import javax.sound.sampled.AudioInputStream;
import javax.swing.*;
import marytts.util.data.audio.AudioPlayer;

public class LocalAudioOutputPlugin implements AudioPlugin {

    AudioPlayer audioPlayer;

    @Override
    public void initialize() {
        audioPlayer = new AudioPlayer();
    }

    @Override
    public boolean isAudioInputPlugin() {
        return false;
    }

    @Override
    public boolean isAudioOutputPlugin() {
        return true;
    }

    @Override
    public String getId() {
        return "dialogos.plugin.localAudioOutput";
    }

    @Override
    public String getName() {
        return "Lokale Audioausgabe";
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
    public PluginSettings createDefaultSettings() {
        return new Settings();
    }

    @Override
    public void playAudio(AudioInputStream audioInputStream) {
        audioPlayer.setAudio(audioInputStream);
        audioPlayer.start();
    }

    @Override
    public void stopAudio() {
        audioPlayer.cancel();
    }

    @Override
    public void joinAudioOutputThread() throws InterruptedException {
        audioPlayer.join();
    }
}
