package com.clt.web;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class DialogOSWebServer {

    public static void startWebServer() {
        new Thread(() -> {
            try {
                Server server = new Server(8080);

                // Needed to serve html files in webapp directory
                ResourceHandler resourceHandler = new ResourceHandler();
                resourceHandler.setResourceBase("webapp");
                resourceHandler.setDirectoriesListed(false);
                resourceHandler.setWelcomeFiles(new String[] { "index.html" });

                ServletContextHandler contextHandler = getServletContextHandler();

                // Add all handlers to the server
                HandlerList handlers = new HandlerList();
                handlers.addHandler(resourceHandler);
                handlers.addHandler(contextHandler);

                server.setHandler(handlers);

                server.start();
                server.join();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }).start();
    }

    private static ServletContextHandler getServletContextHandler() {
        // Manages requests from clients
        ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        contextHandler.setContextPath("/");

        // Register WebSocketRTPBridge under "/audio-stream"
        ServletHolder websocketServletHolder = new ServletHolder(new WebSocketServlet() {
            @Override
            public void configure(WebSocketServletFactory factory) {
                factory.register(WebSocketToRTPBridge.class);
            }
        });
        contextHandler.addServlet(websocketServletHolder, "/audio-stream");
        return contextHandler;
    }
}
