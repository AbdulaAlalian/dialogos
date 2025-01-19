package com.github.dialogos.plugin.remote.output;

import com.clt.dialogos.plugin.AudioPlugin;
import com.clt.dialogos.plugin.PluginSettings;
import com.github.dialogos.plugin.remote.rtp.RTPStreamer;

import javax.sound.sampled.AudioInputStream;
import javax.swing.*;


public class RemoteAudioOutputPlugin implements AudioPlugin {
    RTPStreamer rtpStreamer;

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
        try {
            rtpStreamer.setAudio(audioInputStream);
            rtpStreamer.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    @Override
    public void stopAudio() {
        rtpStreamer.stopStreaming();
    }

    @Override
    public void joinAudioOutputThread() throws InterruptedException {
        rtpStreamer.join();
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
        try {
            //rtpStreamer = new RTPStreamer("127.0.0.1", 5004);
            rtpStreamer = new RTPStreamer("127.0.0.1", 5004);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public PluginSettings createDefaultSettings() {
        return new Settings();
    }
}
