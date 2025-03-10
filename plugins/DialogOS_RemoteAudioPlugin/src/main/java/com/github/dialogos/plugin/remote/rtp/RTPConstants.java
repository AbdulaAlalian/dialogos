package com.github.dialogos.plugin.remote.rtp;

public class RTPConstants {
    // Predefined Payload Types for Common Codecs (for later usage)
    public static final int PCMU = 0;      // PCMU (μ-law)
    public static final int PCMA = 8;      // PCMA (A-law)
    public static final int G711 = 8;      // G.711 (A-law)
    public static final int G729 = 18;     // G.729
    public static final int OPUS = 111;    // Opus (dynamic)

    // Dynamic Payload Type Range (96-127)
    public static final int DYNAMIC_MIN = 96;
    public static final int DYNAMIC_MAX = 127;

    // Standard Port
    public static final int RTP_STANDARD_PORT = 5004;
}
