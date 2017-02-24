package com.example.android.popularmovies.utilities;

import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the movieDB servers.
 */
public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String THEMOVIEDB_BASE_URL =
            "http://api.themoviedb.org/3/movie";

    private static final String TMDB_LANGUAGE = "language";

    private static final String TMDB_PAGE_NUMBER = "page";

    private static final String TMDB_VIDEOS = "videos";

    private static final String TMDB_REVIEWS = "reviews";

    // Get the device language here
    private static String deviceLanguage = Locale.getDefault().getLanguage();

    /**
     * This is where the API KEY from the movieDB is required
     */
    private static final String API_KEY = "";

    /* The API Key parameter required in the url */
    private static final String API_KEY_PARAM = "api_key";

    /**
     * Builds the URL used to talk to the movieDB server using a sort query, a device language and a page number
     *
     * @param sortQuery The sort style (popular/top rated) that will be queried for.
     * @return The URL to use to query the movieDB server.
     */
    public static URL buildUrl(String sortQuery, String currentPageNumber) {
        Uri builtUri = Uri.parse(THEMOVIEDB_BASE_URL).buildUpon()
                .appendPath(sortQuery)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .appendQueryParameter(TMDB_LANGUAGE, deviceLanguage)
                .appendQueryParameter(TMDB_PAGE_NUMBER, currentPageNumber)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI List " + url);

        return url;
    }

    // Check if the API Key is empty or not and returns true or false accordingly
    public static boolean isApiKeyOn() {
        return !API_KEY.equals("");
    }

    /**
     * Builds the URL used to talk to the movieDB server int the detailed activity, once we have a movie ID
     *
     * @param id The id of a movie, once it's clicked
     * @return The URL to use to query the movieDB server.
     */
    public static URL buildUrlDetail(String id) {
        Uri builtUri = Uri.parse(THEMOVIEDB_BASE_URL).buildUpon()
                .appendPath(id)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .appendQueryParameter(TMDB_LANGUAGE, deviceLanguage)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI Detail " + url);

        return url;
    }

    /**
     * Builds the URL used to talk to the movieDB server in the detailed activity, to get the videos once we have a movie ID
     *
     * @param id The id of a movie, once it's clicked
     * @return The URL to use to query the movieDB server.
     */
    public static URL buildUrlVideos(String id) {
        Uri builtUri = Uri.parse(THEMOVIEDB_BASE_URL).buildUpon()
                .appendPath(id)
                .appendPath(TMDB_VIDEOS)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .appendQueryParameter(TMDB_LANGUAGE, deviceLanguage)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI Videos " + url);

        return url;
    }

    /**
     * Builds the URL used to talk to the movieDB server in the detailed activity, to get the reviews once we have a movie ID
     *
     * @param id The id of a movie, once it's clicked
     * @return The URL to use to query the movieDB server.
     */
    public static URL buildUrlReviews(String id) {
        Uri builtUri = Uri.parse(THEMOVIEDB_BASE_URL).buildUpon()
                .appendPath(id)
                .appendPath(TMDB_REVIEWS)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .appendQueryParameter(TMDB_LANGUAGE, deviceLanguage)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI Reviews " + url);

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    /**
     * This method checks if the device is connected to the internet
     *
     * @param handler Changes the mConnected global variable according to connection status
     * @param timeout Time to wait for the server response before considering that the connexion is lost
     */
    public static void isNetworkAvailable(final Handler handler, final int timeout) {
        // Asks for message '0' (not connected) or '1' (connected) on 'handler'
        // the answer must be sent within the 'timeout' (in milliseconds)
        new Thread() {
            private boolean responded = false;
            @Override
            public void run() {
                // set 'responded' to TRUE if is able to connect with google mobile (responds fast)
                new Thread() {
                    @Override
                    public void run() {
                        HttpGet requestForTest = new HttpGet("http://m.google.com");
                        try {
                            new DefaultHttpClient().execute(requestForTest);
                            responded = true;
                        }
                        catch (Exception e) {
                        }
                    }
                }.start();

                try {
                    int waited = 0;
                    while(!responded && (waited < timeout)) {
                        sleep(100);
                        if(!responded ) {
                            waited += 100;
                        }
                    }
                }
                catch(InterruptedException e) {} // do nothing
                finally {
                    if (!responded) { handler.sendEmptyMessage(0); }
                    else { handler.sendEmptyMessage(1); }
                }
            }
        }.start();
    }
}