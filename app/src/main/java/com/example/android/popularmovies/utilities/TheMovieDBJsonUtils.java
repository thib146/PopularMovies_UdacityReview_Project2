package com.example.android.popularmovies.utilities;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.TextView;

import com.example.android.popularmovies.MainActivity;
import com.example.android.popularmovies.MovieDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by thib146 on 20/01/2017.
 */

public class TheMovieDBJsonUtils {

    private static final String TAG = "TheMovieJson";
    /**
     * This method parses JSON from a web response and returns an array of Strings
     * describing the movies with their respective information
     *
     * @param movieJsonStr JSON response from server
     *
     * @param posterVersion Poster size version, depending on the activity
     *
     * @return Array of Strings describing movie data
     *
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static MovieArrays getMovieDataFromJson(Context context, String movieJsonStr, String posterVersion)
            throws JSONException {

        // Page numbers
        final String TMDB_PAGE_NUMBER = "page";
        final String TMDB_TOTAL_PAGES = "total_pages";

        /* Movies information. Each movie info is an element of the "results" array */
        final String TMDB_LIST = "results";
        final String TMDB_POSTER_PATH = "poster_path";
        final String TMDB_BACKDROP_PATH = "backdrop_path";
        final String TMDB_OVERVIEW = "overview";
        final String TMDB_RELEASE_DATE = "release_date";
        final String TMDB_ID = "id";
        final String TMDB_TITLE = "title";
        final String TMDB_ORG_TITLE = "original_title";
        final String TMDB_POPULARITY = "popularity";
        final String TMDB_VOTE_COUNT = "vote_count";
        final String TMDB_VOTE_AVERAGE = "vote_average";

        final String TMDB_VIDEOS = "videos";
        final String TMDB_IMAGES = "images";
        final String TMDB_REVIEWS = "reviews";

        final String TMDB_BASE_URL_POSTER = "http://image.tmdb.org/t/p/";
        final String TMDB_STATUS_CODE = "status_code";

        String backdropSize = "w500";

        // Global Json object
        JSONObject movieJson = new JSONObject(movieJsonStr);

        /* Is there an error? */
        if (movieJson.has(TMDB_STATUS_CODE)) {
            int errorCode = movieJson.getInt(TMDB_STATUS_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* Location invalid */
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }
        }

        // Variable to store all the poster paths URLs
        URL[] urlPosterPath;
        URL[] urlBackdropPath;

        // Json array
        JSONArray movieArray;

        //Movie parsedMovieData = new Movie();
        MovieArrays parsedMovieData = new MovieArrays();

        String totalPagesString;

        if (movieJson.has(TMDB_LIST)) { // If we're reading the results after a query, which means on the Main Activity
            totalPagesString = movieJson.getString(TMDB_TOTAL_PAGES);

            movieArray = movieJson.getJSONArray(TMDB_LIST);

            String currentPage = movieJson.getString(TMDB_PAGE_NUMBER);

            urlPosterPath = new URL[movieArray.length()];

            // Instantiate all the variables that we need
            ArrayList<String> movieTitle = new ArrayList<String>();
            ArrayList<String> posterPath = new ArrayList<String>();
            ArrayList<String> description = new ArrayList<String>();
            ArrayList<String> releaseDate = new ArrayList<String>();
            ArrayList<String> id = new ArrayList<String>();
            ArrayList<String> originalTitle = new ArrayList<String>();
            ArrayList<String> popularity = new ArrayList<String>();
            ArrayList<String> voteCount = new ArrayList<String>();
            ArrayList<String> voteAverage = new ArrayList<String>();

            for (int i = 0; i < movieArray.length(); i++) {

                /* Get the JSON object representing one movie */
                JSONObject oneMovie = movieArray.getJSONObject(i);

                // Copy the read values to the corresponding variables
                movieTitle.add(oneMovie.getString(TMDB_TITLE));
                posterPath.add(oneMovie.getString(TMDB_POSTER_PATH));
                description.add(oneMovie.getString(TMDB_OVERVIEW));
                releaseDate.add(oneMovie.getString(TMDB_RELEASE_DATE));
                id.add(oneMovie.getString(TMDB_ID));
                originalTitle.add(oneMovie.getString(TMDB_ORG_TITLE));
                popularity.add(oneMovie.getString(TMDB_POPULARITY));
                voteCount.add(oneMovie.getString(TMDB_VOTE_COUNT));
                voteAverage.add(oneMovie.getString(TMDB_VOTE_AVERAGE));


                // Remove the first letter from the moviePoser string : the character "/" which is not useful
                String posterPathSubs = posterPath.get(i).substring(1);

                // Build the URI for the Poster Path
                Uri builtUri = Uri.parse(TMDB_BASE_URL_POSTER).buildUpon()
                        .appendPath(posterVersion)
                        .appendPath(posterPathSubs)
                        .build();

                // Create the Poster Path URL
                try {
                    urlPosterPath[i] = new URL(builtUri.toString());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                // Copy the corrected value to the posterPath variable
                posterPath.set(i, urlPosterPath[i].toString());
            }

            // Copy the full data in the parsedMovieData variable
            parsedMovieData.description = description;
            parsedMovieData.id = id;
            parsedMovieData.originalTitle = originalTitle;
            parsedMovieData.popularity = popularity;
            parsedMovieData.posterPath = posterPath;
            parsedMovieData.releaseDate = releaseDate;
            parsedMovieData.title = movieTitle;
            parsedMovieData.voteAverage = voteAverage;
            parsedMovieData.voteCount = voteCount;

            parsedMovieData.totalPageNumber = totalPagesString;

        } else { // If we're reading the info of one movie, which means on the Movie Details activity
            totalPagesString = "1"; // Set the total pages with a random value

            //Copy the read values to the corresponding variables
            String movieTitle = movieJson.getString(TMDB_TITLE);
            String posterPath = movieJson.getString(TMDB_POSTER_PATH);
            String backdropPath = movieJson.getString(TMDB_BACKDROP_PATH);
            String description = movieJson.getString(TMDB_OVERVIEW);
            String releaseDate = movieJson.getString(TMDB_RELEASE_DATE);
            String id = movieJson.getString(TMDB_ID);
            String originalTitle = movieJson.getString(TMDB_ORG_TITLE);
            String popularity = movieJson.getString(TMDB_POPULARITY);
            String voteCount = movieJson.getString(TMDB_VOTE_COUNT);
            String voteAverage = movieJson.getString(TMDB_VOTE_AVERAGE);

            /**
             * Remove the first letter from the movie poster and the backdrop string : the character "/" which is not useful
             */
            posterPath = posterPath.substring(1);
            backdropPath = backdropPath.substring(1);

            urlPosterPath = new URL[movieJson.length()];
            urlBackdropPath = new URL[movieJson.length()];

            // Build the URI for the Poster & Backdrop Paths
            Uri builtUriPoster = Uri.parse(TMDB_BASE_URL_POSTER).buildUpon()
                    .appendPath(posterVersion)
                    .appendPath(posterPath)
                    .build();

            Uri builtUriBackdrop = Uri.parse(TMDB_BASE_URL_POSTER).buildUpon()
                    .appendPath(backdropSize)
                    .appendPath(backdropPath)
                    .build();

            // Build the Poster & Backdrop Paths URLs
            try {
                urlPosterPath[0] = new URL(builtUriPoster.toString());
                urlBackdropPath[0] = new URL(builtUriBackdrop.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            // Copy the built URLs to the posterPath & backdropPath variable
            posterPath = urlPosterPath[0].toString();
            backdropPath = urlBackdropPath[0].toString();

            // Instantiate all the variables that we need
            parsedMovieData.posterPath = new ArrayList<String>();
            parsedMovieData.backdropPath = new ArrayList<String>();
            parsedMovieData.description = new ArrayList<String>();
            parsedMovieData.title = new ArrayList<String>();
            parsedMovieData.releaseDate = new ArrayList<String>();
            parsedMovieData.id = new ArrayList<String>();
            parsedMovieData.originalTitle = new ArrayList<String>();
            parsedMovieData.popularity = new ArrayList<String>();
            parsedMovieData.voteCount = new ArrayList<String>();
            parsedMovieData.voteAverage = new ArrayList<String>();

            // Copy the data in the parsedMovieData variable
            parsedMovieData.description.add(0, description);
            parsedMovieData.id.add(0, id);
            parsedMovieData.originalTitle.add(0, originalTitle);
            parsedMovieData.popularity.add(0, popularity);
            parsedMovieData.posterPath.add(0, posterPath);
            parsedMovieData.backdropPath.add(0, backdropPath);
            parsedMovieData.releaseDate.add(0, releaseDate);
            parsedMovieData.title.add(0, movieTitle);
            parsedMovieData.voteAverage.add(0, voteAverage);
            parsedMovieData.voteCount.add(0, voteCount);
            parsedMovieData.totalPageNumber = totalPagesString;
        }
        // Return the read data
        return parsedMovieData;
    }

    /**
     * This method parses JSON from a web response and returns an array of Strings
     * containing the videos of the selected movie
     *
     * @param movieJsonStr JSON response from server
     *
     * @return Array of Strings containing the videos
     *
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static VideoArrays getVideosFromJson(Context context, String movieJsonStr)
            throws JSONException {

        /* Movies information. Each video info is an element of the "results" array */
        final String TMDB_LIST = "results";
        final String TMDB_MOVIE_ID = "id";

        // Variables used to get the video thumbnail from Youtube
        final String YOUTUBE_IMAGE_BASE_URL = "http://img.youtube.com/vi/";
        final String YOUTUBE_FIRST_IMAGE_FILE = "0.jpg"; // This is the largest image from the different Youtube thumbnails

        final String TMDB_VIDEO_ID = "id";
        final String TMDB_ISO_639_1 = "iso_639_1";
        final String TMDB_ISO_3166_1 = "iso_3166_1";
        final String TMDB_KEY = "key";
        final String TMDB_NAME = "name";
        final String TMDB_SITE = "site";
        final String TMDB_SIZE = "size";
        final String TMDB_TYPE = "type";

        // Variable to store all the video paths URLs
        URL[] urlVideoPath;

        // Global Json object
        JSONObject videoJson = new JSONObject(movieJsonStr);

        // Json array
        JSONArray videoArray;

        VideoArrays parsedVideoData = new VideoArrays();

        String totalPagesString;

        videoArray = videoJson.getJSONArray(TMDB_LIST);

        // Url for the video thumbnail path from Youtube
        urlVideoPath = new URL[videoArray.length()];

        // Instantiate all the variables that we need
        ArrayList<String> videoId = new ArrayList<String>();
        ArrayList<String> iso6391 = new ArrayList<String>();
        ArrayList<String> iso31661 = new ArrayList<String>();
        ArrayList<String> key = new ArrayList<String>();
        ArrayList<String> name = new ArrayList<String>();
        ArrayList<String> site = new ArrayList<String>();
        ArrayList<String> size = new ArrayList<String>();
        ArrayList<String> type = new ArrayList<String>();
        ArrayList<String> videoPath = new ArrayList<String>();

        for (int i = 0; i < videoArray.length(); i++) {

            /* Get the JSON object representing one video */
            JSONObject oneVideo = videoArray.getJSONObject(i);

            // Copy the read values to the corresponding variables
            videoId.add(oneVideo.getString(TMDB_VIDEO_ID));
            iso6391.add(oneVideo.getString(TMDB_ISO_639_1));
            iso31661.add(oneVideo.getString(TMDB_ISO_3166_1));
            key.add(oneVideo.getString(TMDB_KEY));
            name.add(oneVideo.getString(TMDB_NAME));
            site.add(oneVideo.getString(TMDB_SITE));
            size.add(oneVideo.getString(TMDB_SIZE));
            type.add(oneVideo.getString(TMDB_TYPE));

            // Build the URI for the video thumbnail Path
            Uri builtUri = Uri.parse(YOUTUBE_IMAGE_BASE_URL).buildUpon()
                    .appendPath(oneVideo.getString(TMDB_KEY))
                    .appendPath(YOUTUBE_FIRST_IMAGE_FILE)
                    .build();

            // Create the video thumbnail Path URL
            try {
                urlVideoPath[i] = new URL(builtUri.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            // Copy the corrected value to the videoPath variable
            videoPath.add(urlVideoPath[i].toString());
            Log.e(TAG, "Url made :" + urlVideoPath[i].toString());
        }

        // Copy the full data in the parsedVideoData variable
        parsedVideoData.videoId = videoId;
        parsedVideoData.iso6391 = iso6391;
        parsedVideoData.iso31661 = iso31661;
        parsedVideoData.key = key;
        parsedVideoData.name = name;
        parsedVideoData.site = site;
        parsedVideoData.size = size;
        parsedVideoData.type = type;
        parsedVideoData.imagePath = videoPath;

        // Return the read data
        return parsedVideoData;
    }

    /**
     * This method parses JSON from a web response and returns an array of Strings
     * containing the reviews of the selected movie
     *
     * @param reviewJsonStr JSON response from server
     *
     * @return Array of Strings containing the videos
     *
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static ReviewArrays getReviewsFromJson(Context context, String reviewJsonStr)
            throws JSONException {

        /* Movies information. Each movie info is an element of the "results" array */
        final String TMDB_LIST = "results";
        final String TMDB_MOVIE_ID = "id";

        final String TMDB_REVIEW_ID = "id";
        final String TMDB_AUTHOR_NAME = "author";
        final String TMDB_CONTENT = "content";
        final String TMDB_REVIEW_URL = "url";

        // Global Json object
        JSONObject reviewJson = new JSONObject(reviewJsonStr);

        // Json array
        JSONArray reviewArray;

        ReviewArrays parsedReviewData = new ReviewArrays();

        reviewArray = reviewJson.getJSONArray(TMDB_LIST);

        // Instantiate all the variables that we need
        ArrayList<String> author = new ArrayList<String>();
        ArrayList<String> id = new ArrayList<String>();
        ArrayList<String> content = new ArrayList<String>();
        ArrayList<String> url = new ArrayList<String>();

        for (int i = 0; i < reviewArray.length(); i++) {

                /* Get the JSON object representing one movie */
            JSONObject oneReview = reviewArray.getJSONObject(i);

            // Copy the read values to the corresponding variables
            author.add(oneReview.getString(TMDB_AUTHOR_NAME));
            id.add(oneReview.getString(TMDB_REVIEW_ID));
            content.add(oneReview.getString(TMDB_CONTENT));
            url.add(oneReview.getString(TMDB_REVIEW_URL));
        }

        // Copy the full data in the parsedMovieData variable
        parsedReviewData.author = author;
        parsedReviewData.id = id;
        parsedReviewData.content = content;
        parsedReviewData.url = url;

        // Return the read data
        return parsedReviewData;
    }
}