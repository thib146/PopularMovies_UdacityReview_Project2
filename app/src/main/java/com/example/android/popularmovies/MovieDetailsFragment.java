package com.example.android.popularmovies;

import android.support.v4.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.data.PopularMoviesContract;
import com.example.android.popularmovies.utilities.MovieArrays;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.example.android.popularmovies.utilities.ReviewArrays;
import com.example.android.popularmovies.utilities.TheMovieDBJsonUtils;
import com.example.android.popularmovies.utilities.VideoArrays;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.content.res.Configuration.ORIENTATION_PORTRAIT;
import static com.example.android.popularmovies.utilities.NetworkUtils.isNetworkAvailable;

/**
 * Created by thib146 on 21/02/2017.
 */

public class MovieDetailsFragment extends Fragment implements
        MovieAdapter.MovieAdapterOnClickHandler,
        VideoAdapter.VideoAdapterOnClickHandler,
        ReviewAdapter.ReviewAdapterOnClickHandler {

    private static final String TAG = MovieDetailsFragment.class.getSimpleName();

    // Key used to get the movie ID through the fragment creation
    public static final String ARG_ITEM_ID = "item_id";

    private OnItemSelectedListener listener;

    public interface OnItemSelectedListener {
        public void onItemSelected(String movieId);
    }

    private TextView mErrorMessageDisplay;

    private ProgressBar mLoadingIndicator;
    private ProgressBar mVideoLoadingIndicator;
    private ProgressBar mReviewLoadingIndicator;

    private ConstraintLayout mDetailLayout;

    // Global movie variable containing the movie data to be used for the Favorites list
    private MovieArrays mMovie;

    private VideoArrays mVideos;
    private ReviewArrays mReviews;

    // Boolean to know if the movie is already saved in the Favorites
    private boolean isFaved = true;

    private Cursor mCursor;

    // Declare the UI objects
    private TextView mMovieTitle;
    private TextView mMovieOriginalTitle;
    private TextView mReleaseDate;
    private TextView mMovieDescription;
    private TextView mMovieRatings;
    private ImageView mMoviePoster;
    private ImageView mMovieBackdrop;
    private ImageView mFavoritesButton;
    private ImageView mFavedButton;
    private ImageView mDivider1;
    private ImageView mDivider2;
    private ImageView mDivider3;
    private TextView mVideoTitle;
    private TextView mReviewTitle;

    private ArrayList<TextView> mReadMoreButton;

    // No content messages for videos & reviews
    private TextView mVideoNoContentMessage;
    private TextView mReviewNoContentMessage;

    // RecyclerView + Adapter declarations
    private RecyclerView mVideoRecyclerView;
    private VideoAdapter mVideoAdapter;

    private RecyclerView mReviewRecyclerView;
    private ReviewAdapter mReviewAdapter;

    // Only this size of poster will be used
    private String mPosterVersion = "w500";

    // Global variable containing the ID of the movie selected
    private String id = "";

    private static boolean mConnected = true;

    // Columns of data we want to display in the Detailed view
    public static final String[] DETAIL_FAVORITES_PROJECTION = {
            PopularMoviesContract.MovieEntry.COLUMN_MOVIE_ID,
            PopularMoviesContract.MovieEntry.COLUMN_POSTER_PATH,
            PopularMoviesContract.MovieEntry.COLUMN_TITLE
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.movie_detail_fragment, container, false);

        // Initialization of the Favorites buttons (the default and the clicked one)
        mFavoritesButton = (ImageView) view.findViewById(R.id.favorite_button_default);
        mFavedButton = (ImageView) view.findViewById(R.id.favorite_button_clicked);

        // Initialization of the "Read More" button for the long reviews
        mReadMoreButton = new ArrayList<>();

        // Get the intent that started the activity
        Intent intentThatStartedThatActivity = getActivity().getIntent();
        Bundle bundle = getArguments();

        if (bundle != null) { // If the fragment was created in Landscape Mode, get the movie id with the Fragment's arguments
            id = bundle.getString(MovieDetailsFragment.ARG_ITEM_ID);
        } else { // If the fragment was created in Portrait mode (intent), get the movie id with the Intent's extra
            id = intentThatStartedThatActivity.getStringExtra(Intent.EXTRA_TEXT);
        }

        // Store the movie Id in the global variable for other uses
        MainActivity.mId = id;

        // Check if the movie is already a Favorite one by searching for this ID in the database with "query"
        String[] mSelectionArgs = {""};
        mSelectionArgs[0] = id;
        if (id != null) {
            mCursor = getActivity().getContentResolver().query(
                    PopularMoviesContract.MovieEntry.CONTENT_URI,
                    DETAIL_FAVORITES_PROJECTION,
                    PopularMoviesContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                    mSelectionArgs,
                    null);
        }
        if (null == mCursor) { // If the cursor returned is null --> log
            Log.e(TAG, "The search for a favorite movie in the database return a null cursor");
        } else if (id != null && mCursor.getCount() < 1) { // If the cursor has no data, the movie isn't a favorite
            isFaved = false;
            showFavoritesButton();
        } else { // If the cursor has data, the movie is already a favorite
            isFaved = true;
            showFavedButton();
        }


        /**
         * Management of menu buttons
         */
        // BACK BUTTON (ONLY IN PORTRAIT MODE)
        final ImageView backButton = (ImageView) view.findViewById(R.id.iv_back_movie_details);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        // FAVORITE BUTTONS
        // If the default favorite button is clicked -> make this movie a favorite with bulkInsert
        mFavoritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues favValues = new ContentValues();

                favValues.put(PopularMoviesContract.MovieEntry.COLUMN_MOVIE_ID, id);
                favValues.put(PopularMoviesContract.MovieEntry.COLUMN_TITLE, mMovie.title.get(0));
                favValues.put(PopularMoviesContract.MovieEntry.COLUMN_POSTER_PATH, mMovie.posterPath.get(0));

                ArrayList<ContentValues> favValuesContent = new ArrayList<ContentValues>();
                favValuesContent.add(favValues);

                getActivity().getContentResolver().bulkInsert(
                        PopularMoviesContract.MovieEntry.CONTENT_URI,
                        favValuesContent.toArray(new ContentValues[1]));

                Toast.makeText(getActivity(), getString(R.string.toast_add_favorite), Toast.LENGTH_SHORT).show();

                isFaved = true;
                showFavedButton();
            }
        });
        // If the clicked favorite button is clicked -> remove this movie from the favorites with delete
        mFavedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] mSelectionArgs = {""};
                mSelectionArgs[0] = id;
                getActivity().getContentResolver().delete(
                        PopularMoviesContract.MovieEntry.CONTENT_URI,
                        PopularMoviesContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                        mSelectionArgs);

                Toast.makeText(getActivity(), getString(R.string.toast_remove_favorite), Toast.LENGTH_SHORT).show();

                isFaved = false;
                showFavoritesButton();
            }
        });

        // RecyclerView references for the Videos & the Reviews ones
        mVideoRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_videos);
        mReviewRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_reviews);

        final GridLayoutManager videoLayoutManager
                = new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false);

        final GridLayoutManager reviewLayoutManager
                = new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false);

        mVideoRecyclerView.setLayoutManager(videoLayoutManager);
        mReviewRecyclerView.setLayoutManager(reviewLayoutManager);

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mVideoRecyclerView.setHasFixedSize(false);
        mReviewRecyclerView.setHasFixedSize(true);

        mVideoAdapter = new VideoAdapter(this);
        mReviewAdapter = new ReviewAdapter(this);

        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mVideoRecyclerView.setAdapter(mVideoAdapter);
        mReviewRecyclerView.setAdapter(mReviewAdapter);

        // Layout reference
        mDetailLayout = (ConstraintLayout) view.findViewById(R.id.ll_detail_layout);

        /* This TextView is used to display errors and will be hidden if there are no errors */
        mErrorMessageDisplay = (TextView) view.findViewById(R.id.tv_error_message_display_detail);

        // All the loading indicators (Movie Detail activty, Videos view and Reviews view)
        mLoadingIndicator = (ProgressBar) view.findViewById(R.id.pb_loading_indicator_detail);
        mVideoLoadingIndicator = (ProgressBar) view.findViewById(R.id.pb_loading_indicator_videos_detail);
        mReviewLoadingIndicator = (ProgressBar) view.findViewById(R.id.pb_loading_indicator_reviews_detail);

        // UI references
        mMovieTitle = (TextView) view.findViewById(R.id.tv_movie_title_details);
        mMovieOriginalTitle = (TextView) view.findViewById(R.id.tv_movie_original_title_details);
        mReleaseDate = (TextView) view.findViewById(R.id.tv_date_details);
        mMovieDescription = (TextView) view.findViewById(R.id.tv_description_details);
        mMovieRatings = (TextView) view.findViewById(R.id.tv_ratings_details);
        mMoviePoster = (ImageView) view.findViewById(R.id.iv_movie_poster_detail);
        mMovieBackdrop = (ImageView) view.findViewById(R.id.movie_details_toolbar);
        mVideoNoContentMessage = (TextView) view.findViewById(R.id.tv_videos_no_content_message);
        mReviewNoContentMessage = (TextView) view.findViewById(R.id.tv_reviews_no_content_message);
        mDivider1 = (ImageView) view.findViewById(R.id.divider1);
        mDivider2 = (ImageView) view.findViewById(R.id.divider2);
        mDivider3 = (ImageView) view.findViewById(R.id.divider3);
        mVideoTitle = (TextView) view.findViewById(R.id.tv_videos_details);
        mReviewTitle = (TextView) view.findViewById(R.id.tv_reviews_details);

        /**
         * Title and back button display
         */
        //final ImageView backButton = (ImageView) view.findViewById(R.id.iv_back_movie_details);
        TextView activityTitlePortrait = (TextView) view.findViewById(R.id.toolbar_title_movie_details_portrait);
        TextView activityTitleLandscape = (TextView) view.findViewById(R.id.toolbar_title_movie_details_landscape);

        int orientation = getResources().getConfiguration().orientation;

        // Display or hide the back button depending on the device orientation
        if (getResources().getBoolean(R.bool.isTablet) && orientation == ORIENTATION_LANDSCAPE) {
            backButton.setVisibility(View.INVISIBLE);
            activityTitlePortrait.setVisibility(View.INVISIBLE);
            activityTitleLandscape.setVisibility(View.VISIBLE);
        } else if (getResources().getBoolean(R.bool.isTablet) && orientation == ORIENTATION_PORTRAIT) {
            activityTitleLandscape.setVisibility(View.INVISIBLE);
            backButton.setVisibility(View.VISIBLE);
            activityTitlePortrait.setVisibility(View.VISIBLE);
        }

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (id == null) { // If no movie has already been selected, show an empty Detailed Activity, with a "no content" message
            showNoContentMovieDetail();
        } else { // If a movie was previously selected, show the movie data
        /* Once all of our views are setup, we can load the movie data. */
            loadMovieDetailData();
            loadVideosData();
            loadReviewsData();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    /**
     * This method will change mConnected according to the internet connection
     */
    static Handler connectionHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what != 1) { // If not connected
                mConnected = false;
            } else { // If connected
                mConnected = true;
            }
        }
    };

    /**
     * This method will display an empty Detailed Movie activity with a message
     */
    private void showNoContentMovieDetail() {
        mFavoritesButton.setVisibility(View.INVISIBLE);
        mFavedButton.setVisibility(View.INVISIBLE);
        mMovieTitle.setVisibility(View.INVISIBLE);
        mDivider1.setVisibility(View.INVISIBLE);
        mDivider2.setVisibility(View.INVISIBLE);
        mDivider3.setVisibility(View.INVISIBLE);
        mVideoTitle.setVisibility(View.INVISIBLE);
        mReviewTitle.setVisibility(View.INVISIBLE);
        showErrorMessage();
        mDetailLayout.setVisibility(View.VISIBLE);
    }

    /**
     * This method will tell some background method to get
     * the movie details data in the background, with the movie id.
     */
    private void loadMovieDetailData() {
        showMovieDataView();

        if (id != null) { // Check if a movie was previously selected
            new FetchMovieDetailTask().execute(id);
        } else {
            showErrorMessage();
        }
    }

    /**
     * This method will tell some background method to get
     * the videos data in the background, with the movie id.
     */
    private void loadVideosData() {

        if (id != null) { // Check if a movie was previously selected
            new FetchVideosDetailTask().execute(id);
        } else {
            showErrorMessage();
        }
    }

    /**
     * This method will tell some background method to get
     * the reviews data in the background, with the movie id.
     */
    private void loadReviewsData() {

        if (id != null) { // Check if a movie was previously selected
            new FetchReviewsDetailTask().execute(id);
        } else {
            showErrorMessage();
        }
    }

    /**
     * This method will make the View for the movie data visible and
     * hide the error message.
     */
    private void showMovieDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the weather data is visible */
        mDetailLayout.setVisibility(View.VISIBLE);
    }

    // Show the favorite button already clicked
    private void showFavedButton() {
        // Hide the default Favorites button
        mFavoritesButton.setVisibility(View.INVISIBLE);
        // Then, show the Faved button
        mFavedButton.setVisibility(View.VISIBLE);
    }

    // Show the default favorite button
    private void showFavoritesButton() {
        // Hide the Faved button
        mFavedButton.setVisibility(View.INVISIBLE);
        // Then, show the default Favorites button
        mFavoritesButton.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the error message visible and hide the movie
     * View.
     */
    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mDetailLayout.setVisibility(View.INVISIBLE);
        if (!mConnected) {
            mErrorMessageDisplay.setText(R.string.error_message_internet);
        } else if (mMovie == null) {
            mErrorMessageDisplay.setText(R.string.error_message_no_movie_selected);
        } else if (id == null) {
            mErrorMessageDisplay.setText(R.string.error_message_no_movie_selected);
        } else {
            mErrorMessageDisplay.setText(R.string.error_message_common);
        }
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    // Show a special message when there are no videos to display
    private void showVideoNoContent() {
        mVideoNoContentMessage.setVisibility(View.VISIBLE);
    }

    // Show a special message when there are no reviews to display
    private void showReviewsNoContent() {
        mReviewNoContentMessage.setVisibility(View.VISIBLE);
    }

    /**
     * This method is overridden by our MainActivity class in order to handle RecyclerView item
     * clicks.
     *
     * @param itemId The item that was clicked inside a RecyclerView
     */
    @Override
    public void onClick(String itemId) {
        int adapterPosition = mVideoAdapter.adapterPosition;

        // If a video item is clicked, launch the Youtube link (app or browser)
        if (itemId.equals(mVideos.key.get(adapterPosition))) {
            // Launch the Youtube video either in the native app or the internet browser
            launchYoutubeVideo(mVideos.key.get(adapterPosition));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * Method to launch the Youtube video in the Youtube app if installed or in the Internet Browser
     *
     * @param id The Youtube video ID corresponding to the Key number in the Json
     */
    public void launchYoutubeVideo(String id){
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Uri address = Uri.parse("http://www.youtube.com/watch?v=" + id);
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                address);
        try {
            startActivity(appIntent); // launch in the Youtube App
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent); // if the App isn't installed, launch in browser
        }
    }

    /**
     * This method will change the ReleaseDate variable to display it properly
     */
    private String correctReleaseDate(String date) {
        String[] releaseDateSep = date.split("-");
        switch (releaseDateSep[1]) {
            case "01":
                releaseDateSep[1] = getString(R.string.january);
                break;
            case "02":
                releaseDateSep[1] = getString(R.string.february);
                break;
            case "03":
                releaseDateSep[1] = getString(R.string.march);
                break;
            case "04":
                releaseDateSep[1] = getString(R.string.april);
                break;
            case "05":
                releaseDateSep[1] = getString(R.string.may);
                break;
            case "06":
                releaseDateSep[1] = getString(R.string.june);
                break;
            case "07":
                releaseDateSep[1] = getString(R.string.july);
                break;
            case "08":
                releaseDateSep[1] = getString(R.string.august);
                break;
            case "09":
                releaseDateSep[1] = getString(R.string.september);
                break;
            case "10":
                releaseDateSep[1] = getString(R.string.october);
                break;
            case "11":
                releaseDateSep[1] = getString(R.string.november);
                break;
            case "12":
                releaseDateSep[1] = getString(R.string.december);
                break;
        }
        return releaseDateSep[2] + " " + releaseDateSep[1] + " " + releaseDateSep[0];
    }

    // This method will load the movie details in the background and display them in the MovieDetails activity
    public class FetchMovieDetailTask extends AsyncTask<String, Void, MovieArrays> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        protected MovieArrays doInBackground(String... params) {

            /* If there's no data, there's nothing to look up. */
            if (params.length == 0) {
                return null;
            }

            // If there's no internet connexion, stop
            isNetworkAvailable(connectionHandler, 5000);
            if (!mConnected) {
                return null;
            }

            // Create the URL with the current movie ID
            URL movieRequestUrl = NetworkUtils.buildUrlDetail(id);

            try {
                // Get the full HTTP response
                String jsonMovieResponse = NetworkUtils
                        .getResponseFromHttpUrl(movieRequestUrl);

                // Read the movie details from the Json file
                MovieArrays JsonMovieData = TheMovieDBJsonUtils
                        .getMovieDataFromJson(getActivity(), jsonMovieResponse, mPosterVersion);

                // Instantiate all the variables that we need
                MovieArrays movie = new MovieArrays();

                movie.posterPath = new ArrayList<>();
                movie.backdropPath = new ArrayList<>();
                movie.title = new ArrayList<>();
                movie.description = new ArrayList<>();
                movie.originalTitle = new ArrayList<>();
                movie.releaseDate = new ArrayList<>();
                movie.voteAverage = new ArrayList<>();

                // Copy the data from the Json to our movie variable
                movie.posterPath = JsonMovieData.posterPath;
                movie.backdropPath = JsonMovieData.backdropPath;
                movie.title = JsonMovieData.title;
                movie.description = JsonMovieData.description;
                movie.originalTitle = JsonMovieData.originalTitle;
                movie.releaseDate = JsonMovieData.releaseDate;
                movie.voteAverage = JsonMovieData.voteAverage;

                // Store the movie data in a global variable so that we can use it for the Favorites list
                mMovie = movie;

                // Return the movie variable for it to be used in onPostExecute
                return movie;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(MovieArrays movieData) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (movieData != null && isAdded()) {
                showMovieDataView();

                Resources resources = getResources();
                Context contextPoster = mMoviePoster.getContext();
                final Context contextBackdrop = mMovieBackdrop.getContext();

                // Display the movie poster & backdrop
                Picasso.with(contextPoster).load(movieData.posterPath.get(0)).into(mMoviePoster);
                Picasso.with(contextBackdrop).load(movieData.backdropPath.get(0)).into(mMovieBackdrop);
                // Display all the movie info
                mMovieTitle.setText(movieData.title.get(0));
                mMovieOriginalTitle.setText(String.format(resources.getString(R.string.movie_original_title), movieData.originalTitle.get(0)));
                mReleaseDate.setText(String.format(resources.getString(R.string.movie_release_date), correctReleaseDate(movieData.releaseDate.get(0))));
                mMovieDescription.setText(String.format(resources.getString(R.string.movie_description), movieData.description.get(0)));
                mMovieRatings.setText(String.format(resources.getString(R.string.movie_ratings), movieData.voteAverage.get(0)));
            } else {
                showErrorMessage();
            }
        }
    }

    // This method will load the videos of the current movie in the background and send them to the VideoAdapter
    public class FetchVideosDetailTask extends AsyncTask<String, Void, VideoArrays> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mVideoLoadingIndicator.setVisibility(View.VISIBLE);
        }

        protected VideoArrays doInBackground(String... params) {

            /* If there's no data, there's nothing to look up. */
            if (params.length == 0) {
                return null;
            }

            // If there's no internet connexion, stop
            isNetworkAvailable(connectionHandler, 5000);
            if (!mConnected) {
                return null;
            }

            // Create the URL with the current movie ID
            URL videosRequestUrl = NetworkUtils.buildUrlVideos(id);

            try {
                // Get the full HTTP response
                String jsonMovieResponse = NetworkUtils
                        .getResponseFromHttpUrl(videosRequestUrl);

                // Read the videos from the Json file
                VideoArrays JsonMovieData = TheMovieDBJsonUtils
                        .getVideosFromJson(getActivity(), jsonMovieResponse);

                // Instantiate all the variables that we need
                VideoArrays video = new VideoArrays();

                video.videoId = new ArrayList<>();
                video.iso6391 = new ArrayList<>();
                video.iso31661 = new ArrayList<>();
                video.key = new ArrayList<>();
                video.name = new ArrayList<>();
                video.site = new ArrayList<>();
                video.size = new ArrayList<>();
                video.type = new ArrayList<>();
                video.imagePath = new ArrayList<>();

                // Copy the data from the Json to our movie variable
                video.videoId = JsonMovieData.videoId;
                video.iso6391 = JsonMovieData.iso6391;
                video.iso31661 = JsonMovieData.iso31661;
                video.key = JsonMovieData.key;
                video.name = JsonMovieData.name;
                video.site = JsonMovieData.site;
                video.size = JsonMovieData.size;
                video.type = JsonMovieData.type;
                video.imagePath = JsonMovieData.imagePath;

                // Copy the data to the global variable
                mVideos = video;

                // Return the movie variable for it to be used in onPostExecute
                return video;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(VideoArrays videoData) {
            mVideoLoadingIndicator.setVisibility(View.INVISIBLE);
            if (videoData != null && videoData.videoId.size() > 0) {
                mVideoAdapter.setVideoData(videoData); // Send the data to the Adapter
            } else if (videoData == null || videoData.videoId.size() < 1) {
                showVideoNoContent();
            }
        }
    }

    // This method will load the reviews of the current movie in the background and send them to the ReviewAdapter
    public class FetchReviewsDetailTask extends AsyncTask<String, Void, ReviewArrays> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mReviewLoadingIndicator.setVisibility(View.VISIBLE);
        }

        protected ReviewArrays doInBackground(String... params) {

            /* If there's no data, there's nothing to look up. */
            if (params.length == 0) {
                return null;
            }

            // If there's no internet connexion, stop
            isNetworkAvailable(connectionHandler, 5000);
            if (!mConnected) {
                return null;
            }

            // Create the URL with the current movie ID
            URL reviewsRequestUrl = NetworkUtils.buildUrlReviews(id);

            try {
                // Get the full HTTP response
                String jsonReviewResponse = NetworkUtils
                        .getResponseFromHttpUrl(reviewsRequestUrl);

                // Read the reviews from the Json file
                ReviewArrays JsonReviewData = TheMovieDBJsonUtils
                        .getReviewsFromJson(getActivity(), jsonReviewResponse);

                // Instantiate all the variables that we need
                ReviewArrays review = new ReviewArrays();

                review.author = new ArrayList<>();
                review.id = new ArrayList<>();
                review.content = new ArrayList<>();
                review.url = new ArrayList<>();

                // Copy the data from the Json to our movie variable
                review.author = JsonReviewData.author;
                review.id = JsonReviewData.id;
                review.content = JsonReviewData.content;
                review.url = JsonReviewData.url;

                review.readMoreText = getResources().getString(R.string.movie_reviews_read_more);
                review.readLessText = getResources().getString(R.string.movie_reviews_read_less);

                // Copy the data to the global variable
                mReviews = review;

                // Return the movie variable for it to be used in onPostExecute
                return review;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ReviewArrays reviewData) {
            mReviewLoadingIndicator.setVisibility(View.INVISIBLE);
            if (reviewData != null && reviewData.author.size() > 0) {
                mReviewAdapter.setReviewData(reviewData); // Send the data to the Adapter
            } else if (reviewData == null || reviewData.author.size() < 1) {
                showReviewsNoContent();
            }
        }
    }
}