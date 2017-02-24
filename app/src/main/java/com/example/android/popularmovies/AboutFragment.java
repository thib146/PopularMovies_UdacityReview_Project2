package com.example.android.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

/**
 * Created by thib146 on 24/02/2017.
 */

public class AboutFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.about_fragment, container, false);

        /**
         * Management of menu buttons
         */
        // BACK BUTTON
        final ImageView back = (ImageView) view.findViewById(R.id.iv_back_about);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int orientation = getResources().getConfiguration().orientation;

                if (getResources().getBoolean(R.bool.isTablet) && orientation == ORIENTATION_LANDSCAPE) {
                    SettingsFragment settingsFragment = new SettingsFragment();
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.movie_detail_fragment, settingsFragment).commit();
                } else {
                    getActivity().finish();
                }
            }
        });

        return view;
    }
}
