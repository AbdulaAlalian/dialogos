package com.clt.audio;

/*
    TODO
     - (If needed) implement upsampling
 */


import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

public class AudioResampler {
    public static AudioInputStream downsample(AudioInputStream sourceStream, int outputRate) {
        AudioFormat sourceFormat = sourceStream.getFormat();
        AudioFormat targetFormat = new AudioFormat(outputRate, 16, sourceFormat.getChannels(), true, false);

        if (!AudioSystem.isConversionSupported(targetFormat, sourceFormat)) {
            throw new IllegalArgumentException("Downsampling from " + sourceFormat.getSampleRate() + " Hz to " + outputRate + " Hz is not supported.");
        }

        return AudioSystem.getAudioInputStream(targetFormat, sourceStream);
    }
}
