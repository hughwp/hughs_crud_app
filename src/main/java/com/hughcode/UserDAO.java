package com.hughcode;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private Connection conn;

    public UserDAO() {
        conn = DatabaseConnection.getConnection();
    }

    public List<Album> getAlbumsByUser(int userId) throws SQLException {
        String query = "SELECT a.title, a.artist, a.album_cover_link, a.release_year FROM albums a JOIN user_to_albums ua ON a.id = ua.album_id WHERE ua.user_id = " + userId;
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        List<Album> albums = new ArrayList<>();
        while (rs.next()) {
            albums.add(new Album(
                    rs.getString("title"),
                    rs.getString("artist"),
                    rs.getString("album_cover_link"),
                    rs.getString("release_year")
            ));
        }
        stmt.close();
        return albums;
    }
}