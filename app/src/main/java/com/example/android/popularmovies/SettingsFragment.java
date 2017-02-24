package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

/**
 * Created by thib146 on 24/02/2017.
 */

public class SettingsFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.settings_fragment, container, false);

        /**
         * Title and back button display
         */
        ImageView backButton = (ImageView) view.findViewById(R.id.iv_back_settings);
        TextView settingsTitlePortrait = (TextView) view.findViewById(R.id.settings_toolbar_title);
        TextView settingsTitleLandscape = (TextView) view.findViewById(R.id.settings_toolbar_title_landscape);

        int orientation = getResources().getConfiguration().orientation;

        // Display or hide the back button depending on the device orientation
        if (getResources().getBoolean(R.bool.isTablet) && orientation == ORIENTATION_LANDSCAPE) {
            backButton.setVisibility(View.INVISIBLE);
            settingsTitlePortrait.setVisibility(View.INVISIBLE);
            settingsTitleLandscape.setVisibility(View.VISIBLE);
        } else if (getResources().getBoolean(R.bool.isTablet) && orientation == ORIENTATION_PORTRAIT) {
            settingsTitleLandscape.setVisibility(View.INVISIBLE);
            backButton.setVisibility(View.VISIBLE);
            settingsTitlePortrait.setVisibility(View.VISIBLE);
        }

        /**
         * Management of menu buttons
         */
        // BACK BUTTON
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        // ABOUT BUTTON
        final TextView about = (TextView) view.findViewById(R.id.tv_about_button);
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int orientation = getResources().getConfiguration().orientation;

                if (getResources().getBoolean(R.bool.isTablet) && orientation == ORIENTATION_LANDSCAPE) {
                    AboutFragment aboutFragment = new AboutFragment();
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.movie_detail_fragment, aboutFragment).commit();
                } else {
                    Intent intentToStartAboutActivity = new Intent(getActivity(), About.class);
                    startActivity(intentToStartAboutActivity);
                }
            }
        });

        return view;
    }
}
