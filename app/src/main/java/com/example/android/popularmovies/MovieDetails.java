package com.example.android.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

/**
 * Created by thib146 on 22/02/2017.
 */

public class MovieDetails extends AppCompatActivity implements
        MoviesListFragment.OnItemSelectedListener {

    private static final String TAG = MovieDetails.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        // Get the device's orientation
        int orientation = getResources().getConfiguration().orientation;

        // If we're on a Tablet and we rotate the device from landscape to portrait, close this activity
        if (getResources().getBoolean(R.bool.isTablet) && orientation == ORIENTATION_LANDSCAPE) {
            finish();
        }
    }

    // The onClick is handled by the Adapter
    @Override
    public void onItemSelected(String movieId) {

    }
}