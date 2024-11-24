package com.github.dialogos.plugin.input.util;

/**
 * Default Configuration for the microphone class. Used in the default
 * constructor of the microphone
 */
public class MicrophoneDefaultConfig {
    public static final int SAMPLE_RATE = 16000;
    public static final boolean CLOSE_BETWEEN_UTTERANCES = false;
    public static final int MSEC_PER_READ = 10;
    public static final int BITS_PER_SAMPLE = 16;
    public static final int CHANNELS = 1;
    public static final boolean BIG_ENDIAN = false;
    public static final boolean SIGNED = true;
    public static final boolean KEEP_LAST_AUDIO = false;
    public static final String STEREO_TO_MONO = "average";
    public static final int SELECT_CHANNEL = 0;
    public static final String SELECT_MIXER = "default";
    public static final int BUFFER_SIZE = 6400;



}
