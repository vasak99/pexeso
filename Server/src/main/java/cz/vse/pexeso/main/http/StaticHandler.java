package cz.vse.pexeso.main.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class StaticHandler implements HttpHandler {

    public static final Logger log = LoggerFactory.getLogger(StaticHandler.class);

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String filename = System.getProperty("user.dir") + path;
        File file = new File(filename);
        if(file.exists()) {
            Headers headers = exchange.getResponseHeaders();
            String extension = filename.substring(filename.lastIndexOf('.') + 1);
            headers.set("Content-Type", "image/" + extension);
            exchange.sendResponseHeaders(200, file.length());
            OutputStream os = exchange.getResponseBody();
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int count;
            while((count = fis.read(buffer)) != -1) {
                os.write(buffer, 0, count);
            }
            fis.close();
            os.close();
        } else {
            String response = "File not found";
            exchange.sendResponseHeaders(404, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

}
