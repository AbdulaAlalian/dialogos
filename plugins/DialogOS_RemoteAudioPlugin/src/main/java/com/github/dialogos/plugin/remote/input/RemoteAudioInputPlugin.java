package com.github.dialogos.plugin.remote.input;

import com.clt.dialogos.plugin.AudioPlugin;
import com.clt.dialogos.plugin.PluginSettings;
import com.github.dialogos.plugin.remote.rtp.RTPInputStream;
import com.github.dialogos.plugin.remote.rtp.RTPReceiver;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;

public class RemoteAudioInputPlugin implements AudioPlugin {
    private RTPInputStream rtpInputStream;
    private RTPReceiver rtpReceiver;

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
        this.rtpInputStream = new RTPInputStream();
        // TODO add AudioFormat (PCM_SIGNED)
        this.rtpReceiver = new RTPReceiver(null, rtpInputStream);
    }

    @Override
    public PluginSettings createDefaultSettings() {
        return new Settings();
    }

    @Override
    public boolean isRecording() {
        return rtpInputStream.isRecording();
    }

    @Override
    public InputStream setupAndGetAudioInput() throws IOException {
        rtpReceiver.startListening("127.0.0.1", 5004);
        return rtpInputStream;
    }

    @Override
    public void stopRecording() {
        rtpInputStream.stopRecording();
    }

    @Override
    public void startRecording() {
        rtpInputStream.startRecording();
    }
}
