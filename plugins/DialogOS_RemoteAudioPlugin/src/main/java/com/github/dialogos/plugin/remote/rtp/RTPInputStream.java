package com.github.dialogos.plugin.remote.rtp;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class RTPInputStream extends InputStream {
    private final BlockingQueue<byte[]> audioQueue = new LinkedBlockingQueue<>();
    private byte[] currentBuffer = null;
    private int currentBufferPosition = 0;
    private volatile boolean recording = false;

    /**
     * Starts recording, allowing audio data to be added and consumed.
     */
    public void startRecording() {
        recording = true;
    }

    public boolean isRecording() {
        return recording;
    }

    /**
     * Stops recording and signals the end of the stream.
     */
    public void stopRecording() {
        recording = false;
        synchronized (audioQueue) {
            audioQueue.offer(new byte[0]);
        }
    }

    /**
     * Adds audio data to the queue.
     *
     * @param audioData The raw audio data to add.
     */
    public void addAudioData(byte[] audioData) {
        if (recording && audioData != null && audioData.length > 0) {
            audioQueue.offer(audioData);
        }
    }

    @Override
    public int read() throws IOException {
        if (currentBuffer == null || currentBufferPosition >= currentBuffer.length) {
            try {
                currentBuffer = audioQueue.take();
                currentBufferPosition = 0;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException("Thread interrupted while reading audio data.", e);
            }

            if (currentBuffer.length == 0) {
                return -1;
            }
        }

        return currentBuffer[currentBufferPosition++] & 0xFF;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int bytesRead = 0;

        while (bytesRead < len) {
            if (currentBuffer == null || currentBufferPosition >= currentBuffer.length) {
                try {
                    currentBuffer = audioQueue.take();
                    currentBufferPosition = 0;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IOException("Thread interrupted while reading audio data.", e);
                }
                // Signal end of stream
                if (currentBuffer.length == 0) {
                    return bytesRead == 0 ? -1 : bytesRead;
                }
            }

            int bytesToCopy = Math.min(len - bytesRead, currentBuffer.length - currentBufferPosition);
            System.arraycopy(currentBuffer, currentBufferPosition, b, off + bytesRead, bytesToCopy);
            currentBufferPosition += bytesToCopy;
            bytesRead += bytesToCopy;
        }

        return bytesRead;
    }
}
