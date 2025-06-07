package cz.vse.pexeso.main.http;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

/**
 * Http server for easier image transfer
 */
public class ImageServer implements Runnable {

    private HttpServer server;
    private HttpContext staticContext;

    public ImageServer() throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(80), 0);
        this.staticContext = this.server.createContext("/static");

        this.staticContext.setHandler(new StaticHandler());
    }

    @Override
    public void run() {
        this.server.start();
    }

    /**
     * Terminates image server
     */
    public void terminate() {
        this.server.stop(0);
    }

}
