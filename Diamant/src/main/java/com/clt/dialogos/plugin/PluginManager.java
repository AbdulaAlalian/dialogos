package com.clt.dialogos.plugin;

import java.util.Collection;

/*
TODO: Methode, um Plugins zu holen, um damit dann z.B. beim Lokalen
 * AudioInput das mikrofon zu holen und es zum Aufnehmen zu
 * benutzen
 */

public class PluginManager {

    // TODO obviously so ändern, dass er nicht nur das erste element holt
    public static AudioPlugin getActiveAudioInputPlugin() {
        Collection<AudioPlugin> audioPlugins =  PluginLoader.getAudioPlugins();
        if (!audioPlugins.isEmpty()) {
            return audioPlugins.iterator().next();
        }
        return null;
    }
}
