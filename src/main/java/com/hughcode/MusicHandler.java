package com.hughcode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import com.google.gson.Gson;
import com.hughcode.external.MusicAPI;
import com.hughcode.external.LastFMResponse;
import com.hughcode.external.SendResponse;


public class MusicHandler implements HttpHandler {

    Gson gson = new Gson();
    private Connection conn;

    public MusicHandler() {
        conn = DatabaseConnection.getConnection();
    }

    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if ("GET".equals(method) && path.contains("/api/music")) {
            String query = exchange.getRequestURI().getQuery();

            if (query == null || !query.contains("artist") || !query.contains("album")) {
                SendResponse.sendResponse(exchange, 400, "{\"error\": \"Missing artist or album parameters\"}");
                return;
            }

            String[] params = query.split("&");
            String artist = URLDecoder.decode(params[0].split("=")[1], StandardCharsets.UTF_8);
            String album = URLDecoder.decode(params[1].split("=")[1], StandardCharsets.UTF_8);
            handleGetAlbum(exchange, artist, album);
        }
    }

    private void handleGetAlbum(HttpExchange exchange, String artist, String album) throws IOException {
        try {
            LastFMResponse response = MusicAPI.searchForAlbum(album, artist);
            SendResponse.sendResponse(exchange, 200, gson.toJson(response));
        } catch (Exception e) {
            SendResponse.sendResponse(exchange, 500, "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

}