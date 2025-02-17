package com.github.dialogos.plugin.local.input;

import com.clt.dialogos.plugin.PluginRuntime;
import com.clt.dialogos.plugin.PluginSettings;
import com.clt.diamant.IdMap;
import org.xml.sax.SAXException;

import javax.swing.*;
import java.awt.*;

public class Settings extends PluginSettings {

    @Override
    public void writeAttributes(com.clt.xml.XMLWriter out, IdMap uidMap) {
    }

    @Override
    protected void readAttribute(com.clt.xml.XMLReader r, String name, String value, IdMap uid_map) throws SAXException {
    }

    @Override
    public JComponent createEditor() {
        return null;
    }

    @Override
    protected PluginRuntime createRuntime(Component parent) throws Exception {
        return null;
    }


}










































































































































































