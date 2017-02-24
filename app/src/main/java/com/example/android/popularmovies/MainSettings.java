package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

/**
 * Created by thib146 on 21/01/2017.
 */

public class MainSettings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Get the device's orientation
        int orientation = getResources().getConfiguration().orientation;

        // If we're on a Tablet and we rotate the device from landscape to portrait, close this activity
        if (getResources().getBoolean(R.bool.isTablet) && orientation == ORIENTATION_LANDSCAPE) {
            finish();
        }
    }
}