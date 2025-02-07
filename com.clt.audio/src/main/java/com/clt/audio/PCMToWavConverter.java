package com.clt.audio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Class to convert PCM audio to the WAV format
 */
public class PCMToWavConverter {

    /**
     * Converts PCM to WAV, by adding a WAV-Header to the pcm data and wrapping it into a byte array
     * <a href="https://docs.fileformat.com/audio/wav/">Wav-Header Structure</a>
     * @param pcmData data to be converted to wav
     * @param sampleRate sample rate of the pcm-audio
     * @param channels number of channels of the pcm-audio
     * @param bitsPerSample bits per sample of the pcm-audio
     * @return byte array which contains the pcm data wrapped around a wav-header
     */
    public static byte[] convertPcmToWav(byte[] pcmData, int sampleRate, int channels, int bitsPerSample) throws IOException {
        // Create an output stream to write the WAV data to
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // Write the "RIFF" chunk descriptor
        writeString(byteArrayOutputStream, "RIFF"); // ChunkID
        byteArrayOutputStream.write(new byte[4]); // Represents chunk size - Filled in after creation
        writeString(byteArrayOutputStream, "WAVE"); // Format

        // Write the "fmt " subchunk (contains metadata of the audio)
        writeString(byteArrayOutputStream, "fmt "); // Subchunk1ID
        byteArrayOutputStream.write(intToByteArray(16)); // Subchunk1Size (16 for PCM)
        byteArrayOutputStream.write(shortToByteArray((short) 1)); // AudioFormat (1 for PCM)
        byteArrayOutputStream.write(shortToByteArray((short) channels)); // NumChannels
        byteArrayOutputStream.write(intToByteArray(sampleRate)); // SampleRate
        byteArrayOutputStream.write(intToByteArray(sampleRate * channels * bitsPerSample / 8)); // ByteRate (SampleRate * NumChannels * BitsPerSample / 8)
        byteArrayOutputStream.write(shortToByteArray((short) (channels * bitsPerSample / 8))); // BlockAlign (NumChannels * BitsPerSample / 8)
        byteArrayOutputStream.write(shortToByteArray((short) bitsPerSample)); // BitsPerSample (16 bits)

        // Write the "data" subchunk
        writeString(byteArrayOutputStream, "data"); // Subchunk2ID
        byteArrayOutputStream.write(intToByteArray(pcmData.length)); // Subchunk2Size
        byteArrayOutputStream.write(pcmData);

        // return pcm audio wrapped with wav header around it
        return byteArrayOutputStream.toByteArray();
    }

    private static void writeString(ByteArrayOutputStream stream, String str) {
        for (char c : str.toCharArray()) {
            stream.write((byte) c);
        }
    }

    private static byte[] intToByteArray(int value) {
        return new byte[]{
                (byte) (value & 0xFF),
                (byte) ((value >> 8) & 0xFF),
                (byte) ((value >> 16) & 0xFF),
                (byte) ((value >> 24) & 0xFF)
        };
    }

    private static byte[] shortToByteArray(short value) {
        return new byte[]{
                (byte) (value & 0xFF),
                (byte) ((value >> 8) & 0xFF)
        };
    }
}
