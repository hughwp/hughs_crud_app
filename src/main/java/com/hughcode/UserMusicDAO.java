package com.hughcode;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class UserMusicDAO {
    private Connection conn;

    public UserMusicDAO() {
        conn = DatabaseConnection.getConnection();
    }

    public void addAlbumToUser(int userId, int albumId, String rating) throws SQLException {
        String query = "INSERT INTO user_to_albums (user_id, album_id, rating) VALUES (" + userId + ", " + albumId + ", '" + rating + "')";
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(query);
        stmt.close();
    }
}