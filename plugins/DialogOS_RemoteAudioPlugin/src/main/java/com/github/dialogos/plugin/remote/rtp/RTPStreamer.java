package com.github.dialogos.plugin.remote.rtp;

import org.ice4j.socket.MultiplexingDatagramSocket;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Random;

/*
 * TODO
 *  -add WebRTC handling
 *  -(if not fixed with the web feature) fix problem with streaming, where no real audio can be heard
 *        (i think its because of the Payload type)
 * */
public class RTPStreamer extends Thread{
    private static final Random random = new Random();

    private final InetAddress remoteAddress;
    private final int remotePort;
    private MultiplexingDatagramSocket socket;
    private int sequenceNumber;
    private long timestamp;
    private int payloadType = RTPPayloadConstants.DYNAMIC_MIN;
    private int ssrc = random.nextInt(); // SSRC should be unique for the session

    private AudioInputStream audioInputStream;
    private boolean isRunning;

    public RTPStreamer(String remoteAddress, int remotePort) throws IOException {
        this.remoteAddress = InetAddress.getByName(remoteAddress);
        this.remotePort = remotePort;
        this.socket = new MultiplexingDatagramSocket(); // Create RTP socket
    }

    @Override
    public void run() {
        try {
            isRunning = true;
            // sendSDP(remoteAddress.getHostAddress(), remotePort);
            streamAudio();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void streamAudio() throws IOException {
        if (audioInputStream == null) {
            throw new IllegalStateException("No audio stream available");
        }

        AudioFormat format = audioInputStream.getFormat();
        int frameSize = format.getFrameSize();
        int frameRate = (int) format.getFrameRate();
        int bytesPerSecond = frameRate * frameSize;

        byte[] audioBuffer = new byte[576]; // Adjust for MTU if needed (e.g., 1200 bytes max)
        byte[] rtpHeader = new byte[12]; // Standard RTP header size

        while (isRunning) {
            // End stream if no data is available
            int bytesRead = audioInputStream.read(audioBuffer);
            if (bytesRead == -1) break;

            constructRTPHeader(rtpHeader);

            // Combine RTP Header and Payload
            byte[] rtpPacket = new byte[rtpHeader.length + audioBuffer.length];
            System.arraycopy(rtpHeader, 0, rtpPacket, 0, rtpHeader.length);
            System.arraycopy(audioBuffer, 0, rtpPacket, rtpHeader.length, audioBuffer.length);

            // Send RTP Packet
            DatagramPacket packet = new DatagramPacket(
                    rtpPacket,
                    rtpPacket.length,
                    remoteAddress,
                    remotePort
            );
            socket.send(packet);

            // Update RTP state
            sequenceNumber++;
            // timestamp += (audioBuffer.length * 1000) / bytesPerSecond; // Adjust for audio timing
            timestamp += (audioBuffer.length * 8) * 1000 / bytesPerSecond;

            // Sleep to maintain RTP timing
            // long sleepTimeMillis = (long) (bytesRead * 1000.0 / bytesPerSecond);
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        socket.close();
    }

    public void stopStreaming() {
        isRunning = false;
        interrupt();
    }

    private void constructRTPHeader(byte[] header) {
        // RTP Header Construction:
        header[0] = (byte) 0x80; // Version 2, no padding, no extension
        header[1] = (byte) payloadType; // Payload type (dynamic)

        header[2] = (byte) (sequenceNumber >> 8); // Sequence number (high byte)
        header[3] = (byte) sequenceNumber; // Sequence number (low byte)
        // Timestamp (highest byte to lowest byte)
        header[4] = (byte) (timestamp >> 24);
        header[5] = (byte) (timestamp >> 16);
        header[6] = (byte) (timestamp >> 8);
        header[7] = (byte) timestamp;
        // SSRC (highest byte to lowest byte)
        header[8] = (byte) (ssrc >> 24);
        header[9] = (byte) (ssrc >> 16);
        header[10] = (byte) (ssrc >> 8);
        header[11] = (byte) ssrc;
    }

    // Method to generate and send SDP description to the receiver
    private void sendSDP(String ipAddress, int rtpPort) throws IOException {
        String sdpMessage = SDPGenerator.generateSynthesizerSDPDescription(ipAddress, rtpPort);

        // Send SDP message to the receiver (port used is for session initiation protocol)
        DatagramPacket sdpPacket = new DatagramPacket(sdpMessage.getBytes(), sdpMessage.length(),
                InetAddress.getByName(ipAddress), rtpPort);
        socket.send(sdpPacket);
        System.out.println("Sent SDP message to: " + ipAddress + ":5060");
    }

    public void setAudio(AudioInputStream audioInputStream) {
        this.audioInputStream = audioInputStream;
    }
}
