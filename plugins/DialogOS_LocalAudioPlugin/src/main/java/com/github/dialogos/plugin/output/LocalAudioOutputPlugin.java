package com.github.dialogos.plugin.output;

import com.clt.dialogos.plugin.AudioPlugin;
import com.clt.dialogos.plugin.PluginSettings;

import javax.swing.*;

public class LocalAudioOutputPlugin implements AudioPlugin {

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
}
