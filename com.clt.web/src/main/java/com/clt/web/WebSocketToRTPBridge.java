package com.clt.web;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;
import org.eclipse.jetty.websocket.api.annotations.*;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

// TODO Send to RTPReceiver and test
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
        System.out.println("Message received from client " + len + " bytes");
        try (DatagramSocket socket = new DatagramSocket()) {
            // Send to RTP Receiver via UDP
            InetAddress receiverAddress = InetAddress.getByName("localhost");
            DatagramPacket packet = new DatagramPacket(message, offset, message.length, receiverAddress, RTP_PORT);
            socket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();  // Log any error that occurs
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
}
