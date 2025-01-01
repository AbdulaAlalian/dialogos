package com.github.dialogos.plugin.remote.rtp;

import java.io.InputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

public class RTPInputStream extends InputStream {
    private final Queue<byte[]> bufferQueue = new LinkedList<>();
    private byte[] currentBuffer = new byte[0];
    private int currentIndex = 0;
    private boolean recording = false;

    public void addDataToBuffer(byte[] data) {
        synchronized (bufferQueue) {
            bufferQueue.add(data);
        }
    }

    public void startRecording() {
        recording = true;
    }

    public void stopRecording() {
        recording = false;
    }

    @Override
    public int read() throws IOException {
        synchronized (bufferQueue) {
            if (!recording) {
                return -1; // Block reading if not recording
            }

            if (currentIndex >= currentBuffer.length) {
                if (!bufferQueue.isEmpty()) {
                    currentBuffer = bufferQueue.poll(); // Get the next buffer
                    currentIndex = 0;
                } else {
                    return -1; // No data available
                }
            }

            return currentBuffer[currentIndex++] & 0xFF; // Return next byte
        }
    }

    public boolean isRecording() {
        return recording;
    }
}
