package com.clt.web;

import com.clt.audio.PCMToWavConverter;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.concurrent.CopyOnWriteArraySet;

// TODO Clean up code
@WebSocket
public class RTPToWebSocketBridge {

    private boolean isListening = false;
    private static final int RTP_PORT = 5006;
    private static final CopyOnWriteArraySet<Session> sessions = new CopyOnWriteArraySet<>();
    private ByteArrayOutputStream audioBuffer;

    private static final int CHUNK_SIZE = 12800;

    public RTPToWebSocketBridge() {
        startListening();
    }

    public void startListening() {
        isListening = true;
        new Thread(this::listenForPackets).start();
    }

    public void stopListening() {
        isListening = false;
    }

    private void listenForPackets() {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(RTP_PORT);
            byte[] receiveBuffer = new byte[3200];
            DatagramPacket packet = new DatagramPacket(receiveBuffer, receiveBuffer.length);

            System.out.println("Listening for RTP packets on port " + RTP_PORT);

            audioBuffer = new ByteArrayOutputStream();
            while (isListening) {
                socket.receive(packet);

                // Decode RTP packet
                byte[] rtpData = packet.getData();
                int length = packet.getLength();
                byte[] decodedAudio = decodeRTPPacket(rtpData, length);

                if (decodedAudio != null) {
                    audioBuffer.write(decodedAudio);

                    // Send Audio in chunks
                    if (audioBuffer.size() >= CHUNK_SIZE) {
                        sendBufferedAudio();
                    }
                }
            }

            // Ensure remaining buffered data is sent
            if (audioBuffer.size() > 0) {
                sendBufferedAudio();
            }
        } catch (BindException e) {
            System.out.println("Port " + RTP_PORT + " is already in use. Ignoring the bind attempt.");
        } catch (Exception e) {
            System.err.println("Error in RTPToWebSocketBridge: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }

    private void sendBufferedAudio() {
        try {
            byte[] wavData = PCMToWavConverter.convertPcmToWav(audioBuffer.toByteArray(), 16000, 1, 16);
            broadcastAudio(wavData);
            audioBuffer.reset(); // Clear buffer after sending
        } catch (Exception e) {
            System.err.println("Error sending buffered audio: " + e.getMessage());
        }
    }


    private byte[] decodeRTPPacket(byte[] rtpData, int length) {
        try {
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

    private void broadcastAudio(byte[] audioData) {
        for (Session session : sessions) {
            try {
                if (session.isOpen()) {
                    session.getRemote().sendBytes(java.nio.ByteBuffer.wrap(audioData));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("WebSocket Connected: " + session.getRemoteAddress());
        sessions.add(session);
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        System.out.println("WebSocket Closed: " + reason);
        sessions.remove(session);
    }

    @OnWebSocketError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }
}
