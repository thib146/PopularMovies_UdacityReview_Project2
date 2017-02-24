package com.example.android.popularmovies;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

public class MainActivity extends AppCompatActivity implements
        MoviesListFragment.OnItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    // GLobal variable to store the id of a movie
    public static String mId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the device's orientation
        int orientation = getResources().getConfiguration().orientation;

        // On Tablet and on Landscape mode, check if a movie was already selected
        if (getResources().getBoolean(R.bool.isTablet) && orientation == ORIENTATION_LANDSCAPE) {
            if (mId == null) { // If no movie was selected, create a new empty movie detail Fragment, with a "no content" message
                MovieDetailsFragment detailFragment = new MovieDetailsFragment();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.movie_detail_fragment, detailFragment).commit();
            } else { // If a movie was previously selected, get its id and create a Fragment with it in its arguments
                Bundle arguments = new Bundle();
                arguments.putString(MovieDetailsFragment.ARG_ITEM_ID, mId);
                MovieDetailsFragment detailFragment = new MovieDetailsFragment();
                detailFragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_fragment, detailFragment).commit();
            }
        }

    }

    // The onClick is handled by the Adapter
    @Override
    public void onItemSelected(String movieId) {

    }
}