package com.clt.dialogos.plugin;

public interface AudioPlugin extends Plugin {
    boolean isAudioInputPlugin();

    boolean isAudioOutputPlugin();

    // TODO: sicherlich keine void methode daher ändern
    void startRecording();
}
