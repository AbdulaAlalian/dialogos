package com.github.dialogos.plugin.remote.output;

import com.clt.dialogos.plugin.AudioPlugin;
import com.clt.dialogos.plugin.PluginSettings;

import javax.sound.sampled.AudioInputStream;
import javax.swing.*;

public class RemoteAudioOutputPlugin implements AudioPlugin {
    @Override
    public boolean isAudioInputPlugin() {
        return false;
    }

    @Override
    public boolean isAudioOutputPlugin() {
        return true;
    }

    @Override
    public void playAudio(AudioInputStream audioInputStream) {
        // TODO implement playAudio method
    }

    @Override
    public void stopAudio() {
        // TODO implement stopAudio method
    }

    @Override
    public void joinAudioOutputThread() throws InterruptedException {
        // TODO implement joinAudioOutputThread method
    }

    @Override
    public String getId() {
        return "dialogos.plugin.remoteAudioOutput";
    }

    @Override
    public String getName() {
        return "Remote Audioausgabe";
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
    public void initialize() {
        //TODO initialize necessary classes
    }

    @Override
    public PluginSettings createDefaultSettings() {
        return new Settings();
    }
}
