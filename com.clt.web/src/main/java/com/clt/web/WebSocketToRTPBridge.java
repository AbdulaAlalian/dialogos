package com.clt.web;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;

import java.io.IOException;
import java.net.*;


@WebSocket
public class WebSocketToRTPBridge {

    // Port of RTPReceiver
    private static final int RTP_PORT = 5004;

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("Websocket Connected");
    }

    @OnWebSocketMessage
    public void onMessage(Session session, byte[] message, int offset, int len) {
        try {
            // Send to RTP Receiver via UDP
            sendToRTPReceiver(message, offset);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        System.out.println("Connection closed: " + reason);
    }

    @OnWebSocketError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    private void sendToRTPReceiver(byte[] audioData, int offset) {
        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress receiverAddress = InetAddress.getByName("localhost");
            DatagramPacket packet = new DatagramPacket(audioData, offset, audioData.length, receiverAddress, RTP_PORT);
            socket.send(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
