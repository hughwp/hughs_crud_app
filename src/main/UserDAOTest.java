package com.hughcode;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class UserDAOTest {

    @Test
    public void testGetAlbumsByUser() throws Exception {
        UserDAO dao = new UserDAO();

        List<Album> albums = dao.getAlbumsByUser(1);

        assertNotNull(albums);
        assertTrue(albums.size() >= 0);
    }

    @Test
    public void testGetAlbumsByUserEmpty() throws Exception {
        UserDAO dao = new UserDAO();

        List<Album> albums = dao.getAlbumsByUser(1);

        assertNotNull(albums);
        assertEquals(0, albums.size());
    }
}