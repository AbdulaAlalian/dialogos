package com.github.dialogos.plugin.remote.rtp;

import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class RTPReceiver {

    private boolean isListening = false;
    private RTPInputStream rtpInputStream;

    // Constructor
    public RTPReceiver(RTPInputStream rtpInputStream) {
        this.rtpInputStream = rtpInputStream;
    }

    /**
     * Starts listening for rtp streams
     * @param port RTP port
     */
    public void startListening(int port) {
        isListening = true;
        new Thread(() -> listenForPackets(port)).start();
    }

    // Stop listening for RTP streams
    public void stopListening() {
        isListening = false;
        rtpInputStream.stopRecording();
    }

    private void listenForPackets(int port) {
        try (DatagramSocket socket = new DatagramSocket(port)) {
            byte[] receiveBuffer = new byte[3200];
            DatagramPacket packet = new DatagramPacket(receiveBuffer, receiveBuffer.length);

            System.out.println("Listening for RTP packets on port " + port);

            while (isListening) {
                socket.receive(packet);

                // Decode RTP packet
                byte[] rtpData = packet.getData();
                int length = packet.getLength();
                byte[] decodedAudio = decodeRTPPacket(rtpData, length);

                rtpInputStream.addAudioData(decodedAudio);
            }
        } catch (BindException e) {
            System.out.println("Port " + port + " is already in use. Ignoring the bind attempt.");
        }
        catch (Exception e) {
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

            return payload;
        } catch (Exception e) {
            throw new RuntimeException("Error decoding RTP packet: " + e.getMessage());
        }
    }
}
