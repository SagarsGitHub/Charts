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
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

@RestController
public class ChartsController {

    private static final String clientId = "c49891ad14a249c68daa28d26a019b2c";
    private static final String clientSecret = "f6afac8ee2f048f58c5e24e1799bf231";

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