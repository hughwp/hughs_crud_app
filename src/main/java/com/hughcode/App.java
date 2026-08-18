package com.hughcode;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import com.hughcode.external.LastFMResponse;
import com.hughcode.external.MusicAPI;

public class App {

    public static void main(String[] args) throws IOException {

        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.createContext("/api/users", new UserHandler());
            server.createContext("/api/music", new MusicHandler());
            server.setExecutor(null);
            server.start();
            System.out.println("Server running on http://localhost:8080");
        }
        catch(RuntimeException e){
            System.err.println(e.toString());
        }catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}