package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.popularmovies.data.PopularMoviesContract.MovieEntry;

/**
 * Created by thib146 on 13/02/2017.
 */

public class PopularMoviesDBHelper extends SQLiteOpenHelper {
    /*
     * Database name
     */
    public static final String DATABASE_NAME = "popularmovies.db";

    /*
     * Database version
     */
    private static final int DATABASE_VERSION = 1;

    public PopularMoviesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time.
     *
     * @param sqLiteDatabase The database.
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        /*
         * SQL statement that will create a table caching our favorite movies data.
         */
        final String SQL_CREATE_FAVORITES_TABLE =

                "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +

                        MovieEntry._ID                  + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        MovieEntry.COLUMN_MOVIE_ID      + " INTEGER NOT NULL, "                  +

                        MovieEntry.COLUMN_POSTER_PATH   + " STRING NOT NULL,"                    +

                        MovieEntry.COLUMN_TITLE         + " STING NOT NULL);";

        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITES_TABLE);
    }

    /**
     * onUpgrade Method, if the database is modified
     *
     * @param sqLiteDatabase Database that is being upgraded
     * @param oldVersion     The old database version
     * @param newVersion     The new database version
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
