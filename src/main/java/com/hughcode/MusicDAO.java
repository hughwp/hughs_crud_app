package com.hughcode;

import java.sql.*;

public class MusicDAO {
    private Connection conn;

    public MusicDAO() {
        conn = DatabaseConnection.getConnection();
    }

    public int albumExists(String name, String artist) throws SQLException {
        String query = "SELECT id FROM albums WHERE title = '" + name + "' AND artist = '" + artist + "'";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        int albumId = -1;
        if (rs.next()) {
            albumId = rs.getInt("id");
        }
        stmt.close();
        return albumId;
    }

    public int addAlbum(Album album) throws SQLException {
        String insertQuery = "INSERT INTO albums (title, artist, album_cover_link, release_year) VALUES ('"
                + album.name + "', '" + album.artist + "', '" + album.img_url + "', NULL)";

        Statement stmt = conn.createStatement();
        stmt.executeUpdate(insertQuery);
        stmt.close();

        String selectQuery = "SELECT id FROM albums WHERE title = '" + album.name + "' AND artist = '" + album.artist + "'";
        Statement selectStmt = conn.createStatement();
        ResultSet rs = selectStmt.executeQuery(selectQuery);

        int albumId = -1;
        if (rs.next()) {
            albumId = rs.getInt("id");
        }
        selectStmt.close();
        return albumId;
    }
}