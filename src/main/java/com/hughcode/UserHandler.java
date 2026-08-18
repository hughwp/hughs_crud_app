package com.hughcode;
import com.hughcode.external.LastFMResponse;
import com.hughcode.external.MusicAPI;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import com.google.gson.Gson;
import com.hughcode.external.SendResponse;
import com.hughcode.external.LastFMResponse;

public class UserHandler implements HttpHandler {

    Gson gson = new Gson();

    private Connection conn;

    public UserHandler() {
        conn = DatabaseConnection.getConnection();
    }

    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if ("GET".equals(method) && path.equals("/api/users")) {
            handleGetAllUsers(exchange);
        }
        else if ("GET".equals(method) && path.matches("/api/users/\\d+/albums")) {
            String[] parts = path.split("/");
            int userId = Integer.parseInt(parts[3]);
            getUserAlbums(exchange, userId);

        }
        else if ("POST".equals(method) && path.equals("/api/users")) {
            createUser(exchange);
        }
        else if ("POST".equals(method) && path.matches("/api/users/\\d+")) {
            String[] parts = path.split("/");
            int userId = Integer.parseInt(parts[3]);
            String body = new String(exchange.getRequestBody().readAllBytes());

            try {
                Gson gson = new Gson();
                var jsonBody = gson.fromJson(body, java.util.Map.class);
                String artist = (String) jsonBody.get("artist");
                String album = (String) jsonBody.get("album");
                String rating = (String) jsonBody.get("rating");

                postSongToUser(exchange, artist, album, userId, rating);
            } catch (Exception e) {
                SendResponse.sendResponse(exchange, 500, "{\"error\": \"" + e.getMessage() + "\"}");
            }
        }

         else if ("GET".equals(method) && path.matches("/api/users/\\d+")) {
            String[] parts = path.split("/");
            int id = Integer.parseInt(parts[3]);
            getUserById(exchange, id);
        }
    }

    private void handleGetAllUsers(HttpExchange exchange) throws IOException {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM users");

            List<User> users = new ArrayList<User>();

            while (rs.next()) {
                users.add(new User(rs.getInt("id"), rs.getString("username"), rs.getString("email")));
            }

            String users_json = gson.toJson(users);

            System.out.println(rs);

            SendResponse.sendResponse(exchange, 200, users_json);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to connect: " + e.getMessage());
            SendResponse.sendResponse(exchange, 500, "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private void createUser(HttpExchange exchange) throws IOException {
        Gson gson = new Gson();
        String body = new String(exchange.getRequestBody().readAllBytes());
        User user = gson.fromJson(body, User.class);
        SendResponse.sendResponse(exchange, 201, gson.toJson(user));

        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("INSERT INTO users (username, email) VALUES ('" + user.username + "','" + user.email + "')");

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to connect: " + e.getMessage());
            SendResponse.sendResponse(exchange, 500, "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private void getUserById(HttpExchange exchange, int userId) throws IOException {
        Gson gson = new Gson();

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE id = " + userId);

            if (rs.next()) {
                User user = new User(rs.getInt("id"), rs.getString("username"), rs.getString("email"));
                SendResponse.sendResponse(exchange, 200, gson.toJson(user));
            } else {
                SendResponse.sendResponse(exchange, 404, "{\"error\": \"User not found\"}");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to query: " + e.getMessage());
            SendResponse.sendResponse(exchange, 500, "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

private void postSongToUser(HttpExchange exchange, String artist, String album, int userid, String message) throws Exception {
    MusicDAO dao = new MusicDAO();
    UserMusicDAO um_dao = new UserMusicDAO();
    int col_id = dao.albumExists(album, artist);
    Album albumObj = null;

    if (col_id == -1) {
        LastFMResponse response = MusicAPI.searchForAlbum(album, artist);
        albumObj = new Album(
                response.album.name,
                response.album.artist,
                response.album.image[response.album.image.length - 1].text,
                "");
        col_id = dao.addAlbum(albumObj);
    } else {
        SendResponse.sendResponse(exchange, 200, "{\"message\": \"Album already exists in the database. Only Updating many to many table\"}");
    }
    um_dao.addAlbumToUser(userid, col_id, message);
    String jsonResponse = new Gson().toJson(albumObj);
    SendResponse.sendResponse(exchange, 200, jsonResponse);
}

    private void getUserAlbums(HttpExchange exchange, int userId) throws IOException {
        try {
            UserDAO dao = new UserDAO();
            List<Album> albums = dao.getAlbumsByUser(userId);
            String jsonResponse = new Gson().toJson(albums);
            SendResponse.sendResponse(exchange, 200, jsonResponse);
        } catch (SQLException e) {
            SendResponse.sendResponse(exchange, 500, "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}