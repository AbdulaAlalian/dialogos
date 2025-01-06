package com.github.dialogos.plugin.remote.rtp;

import org.ice4j.stack.StunStack;
import javax.sound.sampled.AudioFormat;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * TODO
 *  - Finish RTP handling & receiving
 *  - add decoding for standard audio codecs if needed
 *  - add webRTC support (should not be trivial at all tbh, and requires signaling implementation)
 *
 */

public class RTPReceiver {

    // RTP session management fields
    private StunStack stunStack; // Or an equivalent RTP handler class
    private boolean isListening = false;
    private RTPInputStream rtpInputStream;
    private AudioFormat audioFormat;

    // Constructor
    public RTPReceiver(AudioFormat format, RTPInputStream rtpInputStream) {
        this.audioFormat = format;
        this.rtpInputStream = rtpInputStream;
    }


    // Start listening for RTP streams
    public void startListening(String remoteHost, int port) {
        isListening = true;
        this.rtpInputStream.startRecording();
        new Thread(() -> listenForPackets(port)).start();
    }

    // Stop listening for RTP streams
    public void stopListening() {
        isListening = false;
        rtpInputStream.stopRecording();
    }

    private void listenForPackets(int port) {
        try (DatagramSocket socket = new DatagramSocket(port)) {
            byte[] receiveBuffer = new byte[2048];
            DatagramPacket packet = new DatagramPacket(receiveBuffer, receiveBuffer.length);

            System.out.println("Listening for RTP packets on port " + port);

            while (isListening) {
                socket.receive(packet);

                // Decode the RTP packet
                byte[] rtpData = packet.getData();
                int length = packet.getLength();
                byte[] decodedAudio = decodeRTPPacket(rtpData, length);

                // Add decoded audio to RTPInputStream
                if (decodedAudio != null && rtpInputStream.isRecording()) {
                    rtpInputStream.addDataToBuffer(decodedAudio);
                }
            }
        } catch (Exception e) {
            System.err.println("Error in listenForPackets: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * decodes the received RTPPacket and returns the raw audio data
     * @return decoded raw audio data as a byte array
     */
    private byte[] decodeRTPPacket(byte[] rtpData, int length) {
        try {
            // Parse RTP header and extract payload
            int headerLength = 12;
            if (length < headerLength) {
                return null;
            }

            byte[] payload = new byte[length - headerLength];
            System.arraycopy(rtpData, headerLength, payload, 0, payload.length);

            // TODO add decoding of specific codecs to PCM (G.711 or OPUS) if needed
            return payload;
        } catch (Exception e) {
            throw new RuntimeException("Error decoding RTP packet: " + e.getMessage());
        }
    }
}
