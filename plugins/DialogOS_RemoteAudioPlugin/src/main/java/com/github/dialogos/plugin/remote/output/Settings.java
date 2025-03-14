package com.github.dialogos.plugin.remote.output;

import com.clt.dialogos.plugin.PluginRuntime;
import com.clt.dialogos.plugin.PluginSettings;
import com.clt.diamant.IdMap;
import com.clt.properties.DefaultIntegerProperty;
import com.clt.properties.DefaultStringProperty;
import com.clt.xml.XMLReader;
import com.clt.xml.XMLWriter;
import com.github.dialogos.plugin.remote.rtp.RTPConstants;
import org.xml.sax.SAXException;

import javax.swing.*;
import java.awt.*;

public class Settings extends PluginSettings {
    DefaultIntegerProperty rtpPortProp;
    DefaultStringProperty ipAddrProp;

    public Settings() {
        rtpPortProp = new DefaultIntegerProperty("RTP-Port", "RTP-Port", null, RTPConstants.RTP_STANDARD_PORT);
        ipAddrProp = new DefaultStringProperty("IP-Address", "IP-Address", null, "127.0.0.1");
    }

    @Override
    public void writeAttributes(XMLWriter out, IdMap uidMap) {

    }

    @Override
    protected void readAttribute(XMLReader r, String name, String value, IdMap uid_map) throws SAXException {

    }

    @Override
    public JComponent createEditor() {
        JPanel p = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(2, 3, 2, 3);

        ipAddrProp.addToPanel(p, gbc, false);
        rtpPortProp.addToPanel(p, gbc, false);

        // make editor components stick to top of window
        JPanel superpanel = new JPanel(new BorderLayout());
        superpanel.add(p, BorderLayout.NORTH);

        return superpanel;
    }

    @Override
    protected PluginRuntime createRuntime(Component parent) throws Exception {
        return null;
    }

    public int getRtpPort() {
        return rtpPortProp.getValue();
    }

    public String getIpAddr() {
        return ipAddrProp.getValue();
    }
}
