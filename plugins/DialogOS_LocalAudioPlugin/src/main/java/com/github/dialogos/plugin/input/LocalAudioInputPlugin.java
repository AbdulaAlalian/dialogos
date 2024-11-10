package com.github.dialogos.plugin.input;

import com.clt.dialogos.plugin.AudioPlugin;
import com.clt.dialogos.plugin.PluginSettings;
import com.github.dialogos.plugin.input.util.Microphone;

import javax.swing.*;

/* TODO
    -Verbindung zum PluginManager hinzufügen
 */

public class LocalAudioInputPlugin implements AudioPlugin {
    private Microphone microphone;

    @Override
    public void initialize() {
        microphone = new Microphone();
    }

    @Override
    public String getId() {
        return "dialogos.plugin.localAudioInput";
    }

    // TODO ggf in nen resource ordner umlagern wie bei den anderen
    @Override
    public String getName() {
        return "Lokale Audioeingabe";
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
    public boolean isAudioInputPlugin() {
        return true;
    }

    @Override
    public boolean isAudioOutputPlugin() {
        return false;
    }

    @Override
    public PluginSettings createDefaultSettings() {
        return new Settings();
    }

    @Override
    public void startRecording() {
        microphone.startRecording();
    }

    @Override
    public void stopRecording() {
        microphone.stopRecording();
    }

    @Override
    public boolean isRecording() {
        return microphone.isRecording();
    }

    public Microphone getMicrophone() {
        return microphone;
    }
}
