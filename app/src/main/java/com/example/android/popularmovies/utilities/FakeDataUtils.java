package com.example.android.popularmovies.utilities;

import android.content.ContentValues;
import android.content.Context;

import com.example.android.popularmovies.data.PopularMoviesContract;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by thib146 on 13/02/2017.
 */

public class FakeDataUtils {

    /**
     * Creates a single ContentValues object with random movie data for the provided id
     * @param id movie id
     * @return ContentValues object filled with movie data
     */
    private static ContentValues createTestFavoritesContentValues(long id) {
        ContentValues testFavoritesValues = new ContentValues();
        testFavoritesValues.put(PopularMoviesContract.MovieEntry.COLUMN_MOVIE_ID, id);
        testFavoritesValues.put(PopularMoviesContract.MovieEntry.COLUMN_TITLE, "Interstellar");
        testFavoritesValues.put(PopularMoviesContract.MovieEntry.COLUMN_POSTER_PATH, "http://image.tmdb.org/t/p/w500/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg");
        return testFavoritesValues;
    }

    /**
     * Creates movie data for 6 movies
     * @param context
     */
    public static void insertFakeData(Context context, ArrayList<String> movieId) {

        List<ContentValues> fakeValues = new ArrayList<ContentValues>();
        movieId = new ArrayList<>();
        //loop over 6 movies
        for(int i=0; i<6; i++) {
            fakeValues.add(createTestFavoritesContentValues(341174));
            movieId.add(i, "341174");
        }
        // Bulk Insert our new movie data into PopularMovies' Database
        context.getContentResolver().bulkInsert(
                PopularMoviesContract.MovieEntry.CONTENT_URI,
                fakeValues.toArray(new ContentValues[6]));
    }

    // Method used to delete all the data when we restart the app
    public static void deleteAllData(Context context) {
        context.getContentResolver().delete(PopularMoviesContract.MovieEntry.CONTENT_URI, null, null);
    }
}