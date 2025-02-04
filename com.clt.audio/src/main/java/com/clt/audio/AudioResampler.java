package com.clt.audio;

/*
    TODO
     - (If needed) implement upsampling
 */


import java.io.ByteArrayOutputStream;

public class AudioResampler {
    public static byte[] downsampletest(byte[] input, int inputRate, int outputRate) {
        if (inputRate == outputRate) return input;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int step = inputRate / outputRate;

        // 16-bit samples so divide length by 2 and write both bytes of the sample into the new audio array
        for (int i = 0; i < input.length/2; i += step) {
            baos.write(input[2 * i]);
            baos.write(input[2 * i + 1]);
        }

        return baos.toByteArray();
    }
}
