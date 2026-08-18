package com.hughcode.external;
import com.google.gson.Gson;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import com.hughcode.external.LastFMResponse;

public class MusicAPI {
    private static final String BASE_URL = "http://ws.audioscrobbler.com/2.0";
    private static final String API_KEY = "ed453e7391d001bc70c410838b07be45";
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final Gson gson = new Gson();

    public static LastFMResponse searchForAlbum(String album, String artist) throws Exception {

        String url = BASE_URL + "/?method=album.getinfo&api_key=" + API_KEY
                + "&artist=" + URLEncoder.encode(artist, StandardCharsets.UTF_8)
                + "&album=" + URLEncoder.encode(album, StandardCharsets.UTF_8)
                + "&format=json";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            LastFMResponse my_data = gson.fromJson(response.body(), LastFMResponse.class);
            return my_data;
        } else {
            System.err.println("API call failed: " + response.statusCode());
            return null;

        }

    }
}

