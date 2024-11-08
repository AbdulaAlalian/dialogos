package com.github.dialogos.plugin.input;

import com.clt.dialogos.plugin.PluginRuntime;
import com.clt.dialogos.plugin.PluginSettings;
import com.clt.diamant.IdMap;
import org.xml.sax.SAXException;

import javax.swing.*;
import java.awt.*;

public class Settings extends PluginSettings {

    @Override
    public void writeAttributes(com.clt.xml.XMLWriter out, IdMap uidMap) {
        // TODO settings to save
    }

    @Override
    protected void readAttribute(com.clt.xml.XMLReader r, String name, String value, IdMap uid_map) throws SAXException {
        // TODO settings to read
    }

    // TODO add a real menu not just an empty one
    @Override
    public JComponent createEditor() {
        JPanel p = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(2, 3, 2, 3);

        // make editor components stick to top of window
        JPanel superpanel = new JPanel(new BorderLayout());
        superpanel.add(p, BorderLayout.NORTH);

        return superpanel;
    }

    @Override
    protected PluginRuntime createRuntime(Component parent) throws Exception {
        return null;
    }


}










































































































































































