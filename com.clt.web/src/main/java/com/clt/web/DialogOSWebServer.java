package com.clt.web;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;

// TODO Finish Implementation and Test
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

                // Manages Requests
                ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
                contextHandler.setContextPath("/");

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
}
