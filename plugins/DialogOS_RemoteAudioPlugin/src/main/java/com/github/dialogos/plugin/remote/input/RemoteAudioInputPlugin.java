package com.github.dialogos.plugin.remote.input;

import com.clt.dialogos.plugin.AudioPlugin;
import com.clt.dialogos.plugin.PluginSettings;

import javax.swing.*;

public class RemoteAudioInputPlugin implements AudioPlugin {

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
        return "dialogos.plugin.remoteAudioPlugin";
    }

    @Override
    public String getName() {
        return "Remote Audioeingabe";
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
        AudioPlugin.super.initialize();
    }

    @Override
    public PluginSettings createDefaultSettings() {
        return new Settings();
    }
}
