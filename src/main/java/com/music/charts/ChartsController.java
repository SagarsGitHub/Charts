package com.music.charts;


import org.apache.catalina.connector.Response;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.data.personalization.simplified.GetUsersTopArtistsRequest;


import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner; // Import the Scanner class to read text files
@RestController
public class ChartsController {

    private static final String clientId = "*****";
    private static final String clientSecret = "*****";

    private static final URI redirectUri = SpotifyHttpManager.makeUri("http://localhost:8081/callback");

    private static final SpotifyApi spotifyApi = new SpotifyApi.Builder()
            .setClientId(clientId)
            .setClientSecret(clientSecret)
            .setRedirectUri(redirectUri)
            .build();


    private static String code = "";

    @GetMapping("/test")
    //  creates an authorization url to send to spotify
    public String testEndPoint() {

        StringBuilder authURL = new StringBuilder("https://accounts.spotify.com/authorize");


        authURL.append("?client_id=");
        authURL.append(clientId);
        authURL.append("&response_type=code");
        authURL.append("&redirect_uri=");
        authURL.append(redirectUri);
        authURL.append("&show_dialog=true");
        authURL.append("&scope=user-top-read");

        return authURL.toString();


    }

    @GetMapping("/callback")
    public String callbackEndPoint(@RequestParam(name = "code")String code, HttpServletResponse response) throws URISyntaxException, IOException, InterruptedException {


        AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(code)
                .build();


        try{
            final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();

            spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
            spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());

            System.out.println("successfully updated tokens in the spotify api object");
        } catch (IOException | SpotifyWebApiException | org.apache.hc.core5.http.ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }

        response.sendRedirect("http://localhost:8081/top-artists");
        return spotifyApi.getAccessToken();

    }

    @GetMapping("/top-artists")
    public Artist[] getTopArtists() throws URISyntaxException, IOException, InterruptedException {

        final GetUsersTopArtistsRequest getUsersTopArtistsRequest = spotifyApi.getUsersTopArtists()
                .time_range("medium_term")
                .limit(10)
                .offset(5)
                .build();

        try {
            final Paging<Artist> artistPaging = getUsersTopArtistsRequest.execute();

            return artistPaging.getItems();
        } catch (Exception e) {
            System.out.println("Something malfunctioned!\n\n" + e.getMessage());
        }

        return new Artist[0];
    }
}