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

    // TODO remove menu
    @Override
    public JComponent createEditor() {
        JPanel p = new JPanel(new GridBagLayout());
        return p;
    }

    @Override
    protected PluginRuntime createRuntime(Component parent) throws Exception {
        return null;
    }


}










































































































































































