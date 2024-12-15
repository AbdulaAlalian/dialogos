package com.clt.dialogos.plugin;

import com.clt.diamant.Resources;
import com.clt.properties.DefaultEnumProperty;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

public class PluginManager {

    private static Collection<AudioPlugin> audioPlugins = PluginLoader.getAudioPlugins();

    private DefaultEnumProperty<AudioPlugin> audioInputPluginsProp;

    private DefaultEnumProperty<AudioPlugin> audioOutputPluginsProp;

    public PluginManager() {
        audioInputPluginsProp = new DefaultEnumProperty<>("audioInputPlugin",
                Resources.getString("AudioInputPlugin"), null,
                audioPlugins.stream().filter(AudioPlugin::isAudioInputPlugin).toArray(AudioPlugin[]::new));
        audioOutputPluginsProp =  new DefaultEnumProperty<>("audioOutputPlugin",
                Resources.getString("AudioOutputPlugin"), null,
                audioPlugins.stream().filter(AudioPlugin::isAudioOutputPlugin).toArray(AudioPlugin[]::new));

    }

    public AudioPlugin getActiveAudioInputPlugin(){
        if (audioInputPluginsProp.getValue() != null) {
            return audioInputPluginsProp.getValue();
        }
        return null;
    }

    public AudioPlugin getActiveAudioOutputPlugin(){
        if (audioOutputPluginsProp.getValue() != null) {
            return audioOutputPluginsProp.getValue();
        }
        return null;
    }

    public void setActiveAudioInputPlugin(AudioPlugin plugin) {
        if (plugin.isAudioInputPlugin()) {
            audioInputPluginsProp.setValue(plugin);
        }
    }

    public void setActiveAudioOutputPlugin(AudioPlugin plugin) {
        if (plugin.isAudioOutputPlugin()) {
            audioOutputPluginsProp.setValue(plugin);
        }

    }

    public JComponent createEditor() {
        JPanel p = new JPanel(new GridBagLayout());


        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(2, 3, 2, 3);

        audioInputPluginsProp.addToPanel(p, gbc, false);
        audioOutputPluginsProp.addToPanel(p, gbc, false);

        // make editor components stick to top of window
        JPanel superpanel = new JPanel(new BorderLayout());
        superpanel.add(p, BorderLayout.NORTH);

        return superpanel;
    }
}
