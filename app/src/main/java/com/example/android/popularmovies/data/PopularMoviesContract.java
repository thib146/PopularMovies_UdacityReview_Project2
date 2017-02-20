package com.example.android.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by thib146 on 13/02/2017.
 */

public class PopularMoviesContract {
    /**
     * Content authority for PopularMovies provider.
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.popularmovies";

    /**
     * {@link Uri} on which all other PopularMovies Uris are built.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Path for favorites
     */
    public static final String PATH_FAVORITES = "favorites";

    public static final class MovieEntry implements BaseColumns {

        /**
         * {@link Uri} used to get a full list of favorite movies.
         */
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();

        /**
         * Name of the SQL table for favorites.
         */
        public static final String TABLE_NAME = "favorites";

        /**
         * Column name in the SQLiteDatabase for the movie ID.
         */
        public static final String COLUMN_MOVIE_ID = "movie_id";
        /**
         * Column name in the SQLiteDatabase for the movie poster path.
         */
        public static final String COLUMN_POSTER_PATH = "poster_path";
        /**
         * Column name in the SQLiteDatabase for the movie title.
         */
        public static final String COLUMN_TITLE = "title";

        /**
         * Array containing all the column headers in the favorites table.
         */
        public static final String[] COLUMNS =
                {_ID, COLUMN_MOVIE_ID, COLUMN_POSTER_PATH, COLUMN_TITLE};

        /**
         * This method creates a {@link Uri} for a single movie, referenced by id.
         *
         * @param id The table id of the movie.
         * @return The Uri with the appended id.
         */
        public static Uri buildMovieUriWithId(long id) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(id))
                    .build();
        }
    }
}
